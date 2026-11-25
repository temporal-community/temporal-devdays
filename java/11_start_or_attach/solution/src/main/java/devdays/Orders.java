package devdays;

import io.temporal.client.BatchRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import java.util.UUID;

public class Orders {
    static WorkflowOptions options(String workflowId) {
        return WorkflowOptions.newBuilder()
                .setTaskQueue(WorkerApp.TASK_QUEUE)
                .setWorkflowId(workflowId)
                .build();
    }

    public static TransferWorkflow startTransfer(WorkflowClient client, String transferId) {
        // TODO 1 start: use the business id "transfer-" + transferId as the Workflow ID
        String workflowId = "transfer-" + transferId;
        //? String workflowId = UUID.randomUUID().toString();
        // TODO 1 end
        TransferWorkflow workflow = client.newWorkflowStub(TransferWorkflow.class, options(workflowId));
        WorkflowClient.start(workflow::run);
        return workflow;
    }

    public static OrderWorkflow addItem(WorkflowClient client, String orderId, String item) {
        OrderWorkflow workflow = client.newWorkflowStub(OrderWorkflow.class, options("order-" + orderId));
        // TODO 2 start: start the order and add the item in one call with newSignalWithStartRequest
        BatchRequest request = client.newSignalWithStartRequest();
        request.add(workflow::run);
        request.add(workflow::addItem, item);
        client.signalWithStart(request);
        //? WorkflowClient.start(workflow::run);
        //? workflow.addItem(item);
        // TODO 2 end
        return workflow;
    }
}
