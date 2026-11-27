package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;

public class WorkerApp {
    public static final String TASK_QUEUE = "render";

    static void register(Worker worker) {
        worker.registerWorkflowImplementationTypes(RenderWorkflowImpl.class);
        worker.registerActivitiesImplementations(new RenderActivitiesImpl());
    }

    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        WorkerFactory factory = WorkerFactory.newInstance(client);
        register(factory.newWorker(TASK_QUEUE, WorkerOptions.newBuilder()
                .setMaxConcurrentActivityExecutionSize(Tuning.maxSlots())
                .setMaxWorkerActivitiesPerSecond(Tuning.maxPerSecond())
                .build()));
        factory.start();
    }
}
