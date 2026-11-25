package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.WorkflowReplayer;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeployTest {
    @Test
    void runsRecordedByTheOldCodeStillReplay() {
        try {
            WorkflowReplayer.replayWorkflowExecutionFromResource("history-v1.json", OrderWorkflowImpl.class);
        } catch (Exception e) {
            fail("TODO 1: the new code no longer matches the old history (" + e.getClass().getSimpleName()
                    + "), guard the new step with Workflow.getVersion");
        }
    }

    @Test
    void newRunsCheckTheStock() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            OrderWorkflow workflow = env.getWorkflowClient().newWorkflowStub(
                    OrderWorkflow.class,
                    WorkflowOptions.newBuilder().setWorkflowId("t1").setTaskQueue(WorkerApp.TASK_QUEUE).build());
            assertEquals("baked ticket-Margherita", workflow.run("Margherita"));
            List<String> names = env.getWorkflowClient().fetchHistory("t1").getEvents().stream()
                    .filter(e -> e.hasActivityTaskScheduledEventAttributes())
                    .map(e -> e.getActivityTaskScheduledEventAttributes().getActivityType().getName())
                    .toList();
            assertEquals(List.of("TakeOrder", "CheckStock", "Bake"), names);
        } finally {
            env.close();
        }
    }
}
