package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.temporal.api.enums.v1.EventType;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.Test;

class DurableTest {
    @Test
    void waitsOnATimerThenFinishes() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            OrderWorkflow workflow = env.getWorkflowClient().newWorkflowStub(
                    OrderWorkflow.class,
                    WorkflowOptions.newBuilder().setWorkflowId("t1").setTaskQueue(WorkerApp.TASK_QUEUE).build());
            String result = workflow.run("Margherita", 1800L);
            boolean hasTimer = env.getWorkflowClient().fetchHistory("t1").getEvents().stream()
                    .anyMatch(e -> e.getEventType() == EventType.EVENT_TYPE_TIMER_STARTED);
            assertEquals("baked ticket-Margherita", result);
            assertTrue(hasTimer, "no Timer in the history: TODO 1 is not done");
        } finally {
            env.close();
        }
    }
}
