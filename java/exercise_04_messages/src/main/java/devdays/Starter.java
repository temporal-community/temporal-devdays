package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        TransferWorkflow workflow = client.newWorkflowStub(
                TransferWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("transfer-1")
                        .setTaskQueue(WorkerApp.TASK_QUEUE)
                        .build());
        WorkflowClient.start(workflow::run);
        System.out.println("Started " + WorkflowStub.fromTyped(workflow).getExecution().getWorkflowId());
        System.exit(0);
    }
}
