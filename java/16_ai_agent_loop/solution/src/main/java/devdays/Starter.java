package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        AgentWorkflow workflow = client.newWorkflowStub(
                AgentWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("agent-1")
                        .setTaskQueue(WorkerApp.TASK_QUEUE)
                        .build());
        System.out.println(workflow.run("What is the weather in Pune?"));
        System.exit(0);
    }
}
