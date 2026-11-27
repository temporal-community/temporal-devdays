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
        ClaimWorkflow claim = client.newWorkflowStub(ClaimWorkflow.class, options("claim-1"));
        System.out.println("claim decision: " + claim.run(new Claim(20000, "surgery")));
        OnboardWorkflow onboard = client.newWorkflowStub(OnboardWorkflow.class, options("onboard-ana"));
        System.out.println(onboard.run("Ana"));
        System.exit(0);
    }
}
