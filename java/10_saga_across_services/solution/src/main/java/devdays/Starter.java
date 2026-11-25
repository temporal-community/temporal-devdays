package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) {
        String failAt = args.length > 0 ? args[0] : "";
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        TransferWorkflow workflow = client.newWorkflowStub(
                TransferWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("transfer-" + (failAt.isEmpty() ? "ok" : failAt))
                        .setTaskQueue(OrchestratorWorker.TASK_QUEUE)
                        .build());
        System.out.println(workflow.run(new TransferRequest("alice", "bob", 100, failAt)));
        System.exit(0);
    }
}
