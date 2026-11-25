package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.util.List;

public class Starter {
    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        PizzaWorkflow workflow = client.newWorkflowStub(
                PizzaWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("order-1")
                        .setTaskQueue(WorkerApp.TASK_QUEUE)
                        .build());
        PizzaOrder order = new PizzaOrder(
                new Customer("c1"), List.of(new Pizza("Margherita", 12)), "1 Main St", true, 2);
        System.out.println(workflow.run(order));
        System.exit(0);
    }
}
