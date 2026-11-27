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
        ApprovalWorkflow approval = client.newWorkflowStub(ApprovalWorkflow.class, options("approval-1"));
        WorkflowClient.start(approval::run, 500);
        approval.approve("alice");
        System.out.println(WorkflowStub.fromTyped(approval).getResult(String.class));
        System.exit(0);
    }
}
