package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        OrderWorkflow workflow = client.newWorkflowStub(
                OrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("order-1")
                        .setTaskQueue(WorkerApp.TASK_QUEUE)
                        .build());
        WorkflowClient.start(workflow::run, "Margherita", 1800L);
        System.out.println("Started " + WorkflowStub.fromTyped(workflow).getExecution().getWorkflowId());
        System.exit(0);
    }
}
