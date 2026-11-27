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
        OrderWorkflow order = client.newWorkflowStub(OrderWorkflow.class, options("order-ana"));
        System.out.println(order.run("Ana"));
        System.exit(0);
    }
}
