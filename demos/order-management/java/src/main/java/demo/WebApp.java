package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import demo.Models.OrderInput;
import demo.Models.OrderOutput;
import demo.Models.OrderStatus;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.client.WorkflowUpdateException;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class WebApp {
    static final ObjectMapper JSON = new ObjectMapper();
    private final WorkflowClient client;

    WebApp(WorkflowClient client) {
        this.client = client;
    }

    public static HttpServer create(WorkflowClient client, int port) throws IOException {
        WebApp app = new WebApp(client);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", app::handle);
        return server;
    }

    public static void main(String[] args) throws IOException {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        create(client, 5050).start();
        System.out.println("Order app on http://localhost:5050");
    }

    private void handle(HttpExchange ex) throws IOException {
        String[] path = ex.getRequestURI().getPath().split("/");  // "", "status", "<id>"
        String route = path.length > 1 ? path[1] : "";
        String id = path.length > 2 ? path[2] : "";
        try {
            switch (route) {
                case "" -> send(ex, 200, "text/html; charset=utf-8", page());
                case "start" -> send(ex, 200, start(body(ex)));
                case "status" -> send(ex, 200, status(id));
                case "approve" -> {
                    order(id).approve();
                    send(ex, 200, Map.of("ok", true));
                }
                case "address" -> address(ex, id);
                default -> send(ex, 404, Map.of("error", "not found"));
            }
        } catch (Exception e) {
            send(ex, 500, Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    private Map<String, Object> start(Map<String, String> body) {
        String orderId = UUID.randomUUID().toString().substring(0, 6);
        OrderWorkflow wf = client.newWorkflowStub(OrderWorkflow.class, WorkflowOptions.newBuilder()
            .setTaskQueue(WorkerApp.TASK_QUEUE).setWorkflowId("order-" + orderId).build());
        WorkflowClient.start(wf::run, new OrderInput(orderId, body.get("address"), body.get("scenario")));
        return Map.of("order_id", orderId);
    }

    private Map<String, Object> status(String id) {
        OrderWorkflow wf = order(id);
        OrderStatus s = wf.status();
        WorkflowStub stub = WorkflowStub.fromTyped(wf);
        String state = stub.describe().getStatus().name().replace("WORKFLOW_EXECUTION_STATUS_", "");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("step", s.step());
        out.put("address", s.address());
        out.put("state", state);
        if (state.equals("COMPLETED")) {
            out.put("result", stub.getResult(OrderOutput.class).status());
        }
        return out;
    }

    private void address(HttpExchange ex, String id) throws IOException {
        try {
            order(id).changeAddress(body(ex).get("address"));
            send(ex, 200, Map.of("ok", true, "message", "Address updated"));
        } catch (WorkflowUpdateException e) {
            send(ex, 400, Map.of("ok", false, "message", "Rejected: address is too short"));
        }
    }

    private OrderWorkflow order(String id) {
        return client.newWorkflowStub(OrderWorkflow.class, "order-" + id);
    }

    private static Map<String, String> body(HttpExchange ex) throws IOException {
        byte[] raw = ex.getRequestBody().readAllBytes();
        return raw.length == 0 ? Map.of() : JSON.readValue(raw, JSON.getTypeFactory().constructMapType(Map.class, String.class, String.class));
    }

    private static String page() throws IOException {
        try (var in = WebApp.class.getResourceAsStream("/index.html")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void send(HttpExchange ex, int code, Object json) throws IOException {
        send(ex, code, "application/json", JSON.writeValueAsString(json));
    }

    private static void send(HttpExchange ex, int code, String type, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }
}
