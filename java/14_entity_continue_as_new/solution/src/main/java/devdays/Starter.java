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
        CartWorkflow first = client.newWorkflowStub(CartWorkflow.class, options("cart-1"));
        WorkflowClient.start(first::run, new CartState(List.of(), 1), 3);
        CartWorkflow cart = client.newWorkflowStub(CartWorkflow.class, "cart-1");
        for (int i = 0; i < 7; i++) {
            cart.addItem("item-" + i);
            Thread.sleep(300);
        }
        cart.close();
        CartState state = client.newUntypedWorkflowStub("cart-1").getResult(CartState.class);
        System.out.println("cart has " + state.items().size() + " items after " + state.runs() + " runs");
        System.exit(0);
    }
}
