package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.Test;

class ApprovalTest {
    private ApprovalWorkflow start(TestWorkflowEnvironment env) {
        WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
        env.start();
        return env.getWorkflowClient().newWorkflowStub(
                ApprovalWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue(WorkerApp.TASK_QUEUE).build());
    }

    @Test
    void approvedByAPerson() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            ApprovalWorkflow workflow = start(env);
            WorkflowClient.start(workflow::run, 500);
            workflow.approve("alice");
            assertEquals("approved by alice", WorkflowStub.fromTyped(workflow).getResult(String.class));
        } finally {
            env.close();
        }
    }

    @Test
    void timesOutWhenNobodyAnswers() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            assertEquals("timed out", start(env).run(500));
        } finally {
            env.close();
        }
    }
}
