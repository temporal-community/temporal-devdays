package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.temporal.api.enums.v1.EventType;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import java.util.List;
import org.junit.jupiter.api.Test;

class KitchenTest {
    @Test
    void itemsAreMadeInParallelAndShippedByAChild() {
        KitchenActivitiesImpl.running.set(0);
        KitchenActivitiesImpl.maxRunning.set(0);
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            KitchenWorkflow workflow = env.getWorkflowClient().newWorkflowStub(
                    KitchenWorkflow.class,
                    WorkflowOptions.newBuilder().setWorkflowId("t1").setTaskQueue(WorkerApp.TASK_QUEUE).build());
            List<String> result = workflow.run("o1", List.of("a", "b", "c"));
            List<String> children = env.getWorkflowClient().fetchHistory("t1").getEvents().stream()
                    .filter(e -> e.getEventType() == EventType.EVENT_TYPE_CHILD_WORKFLOW_EXECUTION_STARTED)
                    .map(e -> e.getChildWorkflowExecutionStartedEventAttributes().getWorkflowExecution().getWorkflowId())
                    .toList();
            assertEquals(List.of("a ready", "b ready", "c ready", "shipped o1"), result);
            assertTrue(KitchenActivitiesImpl.maxRunning.get() > 1,
                    "TODO 1: start all the Activities first, then wait for them together");
            assertEquals(List.of("ship-o1"), children, "TODO 2: ship the order in a child Workflow with the id ship-o1");
        } finally {
            env.close();
        }
    }
}
