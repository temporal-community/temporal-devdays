package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class WorkerApp {
    public static final String TASK_QUEUE = "orders";

    static void register(Worker worker) {
        worker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
        worker.registerActivitiesImplementations(new OrderActivitiesImpl());
    }

    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        WorkerFactory factory = WorkerFactory.newInstance(client);
        register(factory.newWorker(TASK_QUEUE));
        factory.start();
    }
}
