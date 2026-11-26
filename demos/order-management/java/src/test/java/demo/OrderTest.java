package demo;

import static org.junit.jupiter.api.Assertions.*;

import demo.Models.OrderInput;
import demo.Models.OrderOutput;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.client.WorkflowUpdateException;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {
    TestWorkflowEnvironment env;
    WorkflowClient client;

    @BeforeEach
    void setUp() {
        ActivitiesImpl.calls.clear();
        env = TestWorkflowEnvironment.newInstance();
        Worker worker = env.newWorker(WorkerApp.TASK_QUEUE);
        WorkerApp.register(worker);
        env.start();
        client = env.getWorkflowClient();
    }

    @AfterEach
    void tearDown() {
        env.close();
    }

    OrderWorkflow start(String scenario) {
        OrderWorkflow wf = client.newWorkflowStub(OrderWorkflow.class,
            WorkflowOptions.newBuilder().setTaskQueue(WorkerApp.TASK_QUEUE)
                .setWorkflowId("order-" + UUID.randomUUID()).build());
        WorkflowClient.start(wf::run, new OrderInput("1", "1 Main St, Redwood", scenario));
        return wf;
    }

    OrderOutput result(OrderWorkflow wf) {
        return WorkflowStub.fromTyped(wf).getResult(OrderOutput.class);
    }

    void waitForStep(OrderWorkflow wf, String step) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            if (wf.status().step().equals(step)) return;
            Thread.sleep(50);
        }
        fail("never reached step: " + step);
    }

    long count(String call) {
        return ActivitiesImpl.calls.stream().filter(call::equals).count();
    }

    @Test
    void happyPathShipsEveryItemWithAChildWorkflow() {
        OrderOutput out = result(start("happy"));
        assertEquals("shipped", out.status());
        assertEquals("TRK-1", out.trackingId());
        assertEquals(3, ActivitiesImpl.calls.stream().filter(c -> c.startsWith("ship:")).count());
    }

    @Test
    void flakyCardSucceedsOnTheThirdAttempt() {
        assertEquals("shipped", result(start("flaky_card")).status());
        assertEquals(3, count("charge"));
    }

    @Test
    void badCardFailsFastAndReleasesTheStock() {
        OrderOutput out = result(start("bad_card"));
        assertEquals("cancelled: card declined", out.status());
        assertEquals(1, count("charge"));
        assertEquals(1, count("release_stock"));
        assertTrue(ActivitiesImpl.calls.stream().noneMatch(c -> c.startsWith("ship:")));
    }

    @Test
    void approvalWaitsForTheSignal() throws Exception {
        OrderWorkflow wf = start("approval");
        waitForStep(wf, "waiting for approval");
        assertEquals(0, count("charge"));
        wf.approve();
        assertEquals("shipped", result(wf).status());
    }

    @Test
    void unapprovedOrderIsCancelledAfterAnHour() {
        OrderOutput out = result(start("approval"));
        assertEquals("cancelled: approval timed out", out.status());
        assertEquals(1, count("release_stock"));
    }

    @Test
    void updateChangesTheShippingAddress() throws Exception {
        OrderWorkflow wf = start("approval");
        waitForStep(wf, "waiting for approval");
        wf.changeAddress("9 New Road, Austin");
        wf.approve();
        assertEquals("9 New Road, Austin", result(wf).address());
    }

    @Test
    void updateWithABadAddressIsRejected() throws Exception {
        OrderWorkflow wf = start("approval");
        waitForStep(wf, "waiting for approval");
        assertThrows(WorkflowUpdateException.class, () -> wf.changeAddress("x"));
        assertEquals("1 Main St, Redwood", wf.status().address());
    }
}
