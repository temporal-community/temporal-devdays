package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.temporal.api.enums.v1.EventType;
import io.temporal.api.history.v1.HistoryEvent;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CartTest {
    private static void waitForNewRun(WorkflowClient client, String workflowId, String firstRunId)
            throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            List<HistoryEvent> events = client.fetchHistory(workflowId, firstRunId).getEvents();
            if (events.get(events.size() - 1).getEventType() == EventType.EVENT_TYPE_WORKFLOW_EXECUTION_CONTINUED_AS_NEW) {
                return;
            }
            Thread.sleep(100);
        }
        fail("TODO 1: the Workflow never continued as new after 10 items");
    }

    @Test
    void theCartKeepsEveryItemAcrossRuns() throws Exception {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            WorkflowClient client = env.getWorkflowClient();
            CartWorkflow first = client.newWorkflowStub(
                    CartWorkflow.class,
                    WorkflowOptions.newBuilder().setWorkflowId("cart-1").setTaskQueue(WorkerApp.TASK_QUEUE).build());
            String firstRunId = WorkflowClient.start(first::run, new CartState(List.of(), 1), 10).getRunId();
            CartWorkflow latest = client.newWorkflowStub(CartWorkflow.class, "cart-1");
            List<String> expected = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                latest.addItem("item-" + i);
                expected.add("item-" + i);
                if (i == 9) {
                    waitForNewRun(client, "cart-1", firstRunId);
                }
            }
            latest.close();
            CartState result = client.newUntypedWorkflowStub("cart-1").getResult(CartState.class);
            assertEquals(expected, result.items());
            assertTrue(result.runs() >= 2, "TODO 1: continue as new after maxPerRun items, carrying the items forward");
        } finally {
            env.close();
        }
    }
}
