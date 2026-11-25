package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        GreetSomeone workflow = client.newWorkflowStub(
                GreetSomeone.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("my-first-workflow")
                        .setTaskQueue(WorkerApp.TASK_QUEUE)
                        .build());
        System.out.println(workflow.run("Temporal"));
        System.exit(0);
    }
}
