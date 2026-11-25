package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.temporal.api.enums.v1.EventType;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.Test;

class RetriesTest {
    private PaymentWorkflow start(TestWorkflowEnvironment env, String id) {
        WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
        env.start();
        return env.getWorkflowClient().newWorkflowStub(
                PaymentWorkflow.class,
                WorkflowOptions.newBuilder().setWorkflowId(id).setTaskQueue(WorkerApp.TASK_QUEUE).build());
    }

    private static String root(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return String.valueOf(t.getMessage());
    }

    @Test
    void flakyCardSucceedsOnThirdAttemptWithARetryPolicy() {
        ChargeActivitiesImpl.calls.clear();
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            String result = start(env, "t1").run("flaky");
            assertEquals("charged on attempt 3", result);
            int max = env.getWorkflowClient().fetchHistory("t1").getEvents().stream()
                    .filter(e -> e.getEventType() == EventType.EVENT_TYPE_ACTIVITY_TASK_SCHEDULED)
                    .findFirst().orElseThrow()
                    .getActivityTaskScheduledEventAttributes().getRetryPolicy().getMaximumAttempts();
            assertEquals(5, max, "TODO 1: set the maximum attempts to 5 in RetryOptions");
        } finally {
            env.close();
        }
    }

    @Test
    void badCardIsNotRetried() {
        ChargeActivitiesImpl.calls.clear();
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            PaymentWorkflow workflow = start(env, "t2");
            WorkflowFailedException e = assertThrows(WorkflowFailedException.class, () -> workflow.run("bad"));
            assertEquals(1, ChargeActivitiesImpl.calls.size(),
                    "charge was retried " + ChargeActivitiesImpl.calls.size() + " times: " + root(e));
        } finally {
            env.close();
        }
    }
}
