package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.client.WorkflowUpdateException;
import io.temporal.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.Test;

class MessagesTest {
    @Test
    void signalQueryAndUpdate() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            TransferWorkflow workflow = env.getWorkflowClient().newWorkflowStub(
                    TransferWorkflow.class,
                    WorkflowOptions.newBuilder().setTaskQueue(WorkerApp.TASK_QUEUE).build());
            WorkflowClient.start(workflow::run);
            assertEquals("waiting", workflow.getStatus());
            assertEquals(150, workflow.setAmount(150));
            assertThrows(WorkflowUpdateException.class, () -> workflow.setAmount(-5));
            workflow.approve();
            assertEquals("sent 150", WorkflowStub.fromTyped(workflow).getResult(String.class));
        } finally {
            env.close();
        }
    }
}
