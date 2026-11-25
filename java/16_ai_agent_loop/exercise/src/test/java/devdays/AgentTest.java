package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.temporal.api.history.v1.ActivityTaskScheduledEventAttributes;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgentTest {
    @Test
    void theModelAndTheToolRunAsActivities() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            AgentWorkflow workflow = env.getWorkflowClient().newWorkflowStub(
                    AgentWorkflow.class,
                    WorkflowOptions.newBuilder().setWorkflowId("t1").setTaskQueue(WorkerApp.TASK_QUEUE).build());
            String answer = workflow.run("What is the weather in Pune?");
            List<ActivityTaskScheduledEventAttributes> scheduled =
                    env.getWorkflowClient().fetchHistory("t1").getEvents().stream()
                            .filter(e -> e.hasActivityTaskScheduledEventAttributes())
                            .map(e -> e.getActivityTaskScheduledEventAttributes())
                            .toList();
            List<String> names = scheduled.stream().map(s -> s.getActivityType().getName()).toList();
            assertEquals("It is sunny in Pune", answer);
            assertEquals(2, names.stream().filter(n -> n.equals("LlmDecide")).count(),
                    "TODO 1: the model call must be an Activity, not plain code in the Workflow");
            assertEquals(1, names.stream().filter(n -> n.equals("RunTool")).count(),
                    "TODO 2: the tool call must be an Activity, not plain code in the Workflow");
            int max = scheduled.stream().filter(s -> s.getActivityType().getName().equals("LlmDecide"))
                    .findFirst().orElseThrow().getRetryPolicy().getMaximumAttempts();
            assertEquals(3, max, "TODO 1: retry the model call at most 3 times");
        } finally {
            env.close();
        }
    }
}
