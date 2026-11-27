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
        KitchenWorkflow kitchen = client.newWorkflowStub(KitchenWorkflow.class, options("kitchen-o1"));
        System.out.println(kitchen.run("o1", List.of("a", "b", "c")));
        System.exit(0);
    }
}
