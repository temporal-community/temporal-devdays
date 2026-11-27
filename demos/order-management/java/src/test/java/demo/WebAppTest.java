package demo;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpServer;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.WorkerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebAppTest {
    TestWorkflowEnvironment env;
    HttpServer server;
    HttpClient http = HttpClient.newHttpClient();
    String base;

    @BeforeEach
    void setUp() throws Exception {
        env = TestWorkflowEnvironment.newInstance();
        WorkerApp.register(queue -> env.newWorker(queue, WorkerOptions.newBuilder().setIdentity("java-" + queue).build()), WorkerApp.SERVICES);
        env.start();
        server = WebApp.create(env.getWorkflowClient(), 0);
        server.start();
        base = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
        env.close();
    }

    HttpResponse<String> call(String method, String path, String json) throws Exception {
        var req = HttpRequest.newBuilder(URI.create(base + path))
            .method(method, json == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json))
            .header("Content-Type", "application/json").build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    String startOrder(String scenario) throws Exception {
        String json = "{\"scenario\":\"" + scenario + "\",\"address\":\"1 Main St, Redwood\"}";
        return WebApp.JSON.readTree(call("POST", "/start", json).body()).get("order_id").asText();
    }

    JsonNode waitFor(String id, String field, String value) throws Exception {
        for (int i = 0; i < 100; i++) {
            JsonNode s = WebApp.JSON.readTree(call("GET", "/status/" + id, null).body());
            if (s.get(field).asText().equals(value)) return s;
            Thread.sleep(50);
        }
        throw new AssertionError("order " + id + " never had " + field + " = " + value);
    }

    @Test
    void servesTheSamePageAsThePythonApp() throws Exception {
        String python = Files.readString(Path.of("../python/templates/index.html"));
        assertEquals(python, call("GET", "/", null).body(), "copy python/templates/index.html to src/main/resources/index.html");
    }

    @Test
    void happyOrderShips() throws Exception {
        JsonNode s = waitFor(startOrder("happy"), "state", "COMPLETED");
        assertEquals("shipped", s.get("result").asText());
        assertEquals("done", s.get("step").asText());
    }

    @Test
    void approvalAndAddressUpdateWork() throws Exception {
        String id = startOrder("approval");
        waitFor(id, "step", "waiting for approval");
        assertEquals(400, call("POST", "/address/" + id, "{\"address\":\"x\"}").statusCode());
        assertEquals(200, call("POST", "/address/" + id, "{\"address\":\"9 New Road, Austin\"}").statusCode());
        call("POST", "/approve/" + id, "{}");
        JsonNode s = waitFor(id, "state", "COMPLETED");
        assertEquals("9 New Road, Austin", s.get("address").asText());
    }

    JsonNode view(String id, Predicate<JsonNode> until) throws Exception {
        JsonNode data = null;
        for (int i = 0; i < 200; i++) {
            data = WebApp.JSON.readTree(call("GET", "/timeline/" + id, null).body());
            if (until.test(data)) return data;
            Thread.sleep(50);
        }
        throw new AssertionError("timeline never reached the expected state: " + data);
    }

    List<String> names(JsonNode data, String field, String value) {
        List<String> out = new ArrayList<>();
        for (JsonNode s : data.get("steps")) {
            if (s.get(field).asText().equals(value)) out.add(s.get("name").asText());
        }
        return out;
    }

    @Test
    void theTimelineShowsTheStepsTheChildAndWhoRanThem() throws Exception {
        JsonNode data = view(startOrder("happy"), d -> d.get("state").asText().equals("COMPLETED"));
        assertEquals(List.of("check_stock", "reserve_stock", "charge"), names(data, "kind", "activity"));
        for (JsonNode s : data.get("steps")) {
            if (s.get("kind").asText().equals("activity")) assertEquals("java", s.get("language").asText());
        }
        JsonNode child = null;
        for (JsonNode s : data.get("steps")) if (s.get("kind").asText().equals("child")) child = s;
        assertEquals("done", child.get("status").asText());
        assertEquals(3, child.get("items").size());
        for (JsonNode item : child.get("items")) {
            assertEquals("done", item.get("status").asText());
            assertEquals("shipping", item.get("service").asText());
        }
    }

    @Test
    void theTimelineShowsTheUndoStepsInReverseOrder() throws Exception {
        JsonNode data = view(startOrder("ship_fails"), d -> d.get("state").asText().equals("COMPLETED"));
        assertEquals(List.of("refund_charge", "release_stock"), names(data, "undo", "true"));
        for (JsonNode s : data.get("steps")) {
            if (s.get("kind").asText().equals("child")) assertEquals("failed", s.get("status").asText());
        }
    }

    @Test
    void theApprovalCountdownAndTheMessageSteps() throws Exception {
        String id = startOrder("approval");
        view(id, d -> !d.get("countdown_ends").isNull());
        call("POST", "/address/" + id, "{\"address\":\"99 Long Road, Pune\"}");
        call("POST", "/approve/" + id, "{}");
        JsonNode data = view(id, d -> d.get("state").asText().equals("COMPLETED"));
        assertEquals(List.of("change_address"), names(data, "kind", "update"));
        assertEquals(List.of("approve"), names(data, "kind", "signal"));
        assertTrue(data.get("countdown_ends").isNull());
    }
}
