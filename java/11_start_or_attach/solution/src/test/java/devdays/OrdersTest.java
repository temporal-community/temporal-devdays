package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrdersTest {
    private TestWorkflowEnvironment start() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
        env.start();
        return env;
    }

    @Test
    void aSecondStartOfTheSameTransferIsRejected() {
        TestWorkflowEnvironment env = start();
        try {
            WorkflowClient client = env.getWorkflowClient();
            TransferWorkflow first = Orders.startTransfer(client, "t1");
            assertEquals("transfer-t1", WorkflowStub.fromTyped(first).getExecution().getWorkflowId(),
                    "TODO 1: use the business id as the Workflow ID");
            assertThrows(WorkflowExecutionAlreadyStarted.class, () -> Orders.startTransfer(client, "t1"),
                    "TODO 1: a second start for the same transfer must be rejected");
        } finally {
            env.close();
        }
    }

    @Test
    void addItemStartsOrAttaches() {
        TestWorkflowEnvironment env = start();
        try {
            OrderWorkflow order = null;
            try {
                Orders.addItem(env.getWorkflowClient(), "o1", "pizza");
                order = Orders.addItem(env.getWorkflowClient(), "o1", "cola");
            } catch (WorkflowExecutionAlreadyStarted e) {
                fail("TODO 2: the second call failed (" + e.getMessage()
                        + "), use signal-with-start so it starts or attaches");
            }
            order.close();
            assertEquals(List.of("pizza", "cola"), WorkflowStub.fromTyped(order).getResult(List.class));
        } finally {
            env.close();
        }
    }
}
