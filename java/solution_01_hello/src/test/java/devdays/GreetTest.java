package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.Test;

class GreetTest {
    @Test
    void greets() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE));
            env.start();
            GreetSomeone workflow = env.getWorkflowClient().newWorkflowStub(
                    GreetSomeone.class,
                    WorkflowOptions.newBuilder().setTaskQueue(WorkerApp.TASK_QUEUE).build());
            assertEquals("Hello, Temporal!", workflow.run("Temporal"));
        } finally {
            env.close();
        }
    }
}
