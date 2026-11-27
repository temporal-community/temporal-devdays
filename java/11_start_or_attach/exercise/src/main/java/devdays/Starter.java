package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.util.List;

public class Starter {
    static WorkflowOptions options(String id) {
        return WorkflowOptions.newBuilder().setWorkflowId(id).setTaskQueue(WorkerApp.TASK_QUEUE).build();
    }

    public static void main(String[] args) throws Exception {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        TransferWorkflow transfer = Orders.startTransfer(client, "42");
        try {
            Orders.startTransfer(client, "42");
        } catch (io.temporal.client.WorkflowExecutionAlreadyStarted e) {
            System.out.println("second start rejected");
        }
        transfer.approve();
        System.out.println("transfer: " + WorkflowStub.fromTyped(transfer).getResult(String.class));
        Orders.addItem(client, "7", "pizza");
        OrderWorkflow order = Orders.addItem(client, "7", "cola");
        order.close();
        System.out.println("order items: " + WorkflowStub.fromTyped(order).getResult(List.class));
        System.exit(0);
    }
}
