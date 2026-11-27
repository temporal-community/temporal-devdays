package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.util.List;
import java.util.stream.IntStream;

public class Starter {
    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        RenderWorkflow workflow = client.newWorkflowStub(
                RenderWorkflow.class,
                WorkflowOptions.newBuilder().setWorkflowId("render-30").setTaskQueue(WorkerApp.TASK_QUEUE).build());
        long start = System.nanoTime();
        List<String> result = workflow.run(IntStream.range(0, 30).mapToObj(i -> "item-" + i).toList());
        System.out.printf("rendered %d items in %.1f seconds%n", result.size(), (System.nanoTime() - start) / 1e9);
        System.exit(0);
    }
}
