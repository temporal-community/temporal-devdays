package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.Test;

class DesignTest {
    private <W> W stub(TestWorkflowEnvironment env, Class<W> type) {
        return env.getWorkflowClient().newWorkflowStub(
                type, WorkflowOptions.newBuilder().setTaskQueue(WorkerApp.TASK_QUEUE).build());
    }

    @Test
    void pureLogicStaysInTheWorkflow() {
        ClaimActivitiesImpl.calls.clear();
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            assertEquals("review", stub(env, ClaimWorkflow.class).run(new Claim(20000, "dental")));
            assertEquals("approve", stub(env, ClaimWorkflow.class).run(new Claim(100, "dental")));
            assertEquals(0, ClaimActivitiesImpl.calls.size(),
                    "TODO 1: the decision is plain logic, keep it in the Workflow, not in an Activity");
        } finally {
            env.close();
        }
    }

    @Test
    void splitTheActivityThatCallsTwoServices() {
        OnboardActivitiesImpl.users.clear();
        OnboardActivitiesImpl.emails.clear();
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            assertEquals("welcome", stub(env, OnboardWorkflow.class).run("alice"));
            assertEquals(1, OnboardActivitiesImpl.users.size(),
                    "TODO 2: the user was added " + OnboardActivitiesImpl.users.size()
                            + " times, split the Activity so a retry only repeats the email");
            assertEquals(1, OnboardActivitiesImpl.emails.size());
        } finally {
            env.close();
        }
    }
}
