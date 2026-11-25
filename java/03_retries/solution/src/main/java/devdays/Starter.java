package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) {
        String card = args.length > 0 ? args[0] : "flaky";
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        PaymentWorkflow workflow = client.newWorkflowStub(
                PaymentWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("payment-" + card)
                        .setTaskQueue(WorkerApp.TASK_QUEUE)
                        .build());
        System.out.println(workflow.run(card));
        System.exit(0);
    }
}
