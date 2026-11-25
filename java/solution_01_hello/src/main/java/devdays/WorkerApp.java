package devdays;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class WorkerApp {
    public static final String TASK_QUEUE = "hello-world";

    static void register(Worker worker) {
        // TODO 1 start: register GreetSomeoneImpl as the workflow and GreetingActivitiesImpl as the activities
        worker.registerWorkflowImplementationTypes(GreetSomeoneImpl.class);
        worker.registerActivitiesImplementations(new GreetingActivitiesImpl());
        // TODO 1 end
    }

    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        WorkerFactory factory = WorkerFactory.newInstance(client);
        register(factory.newWorker(TASK_QUEUE));
        factory.start();
    }
}
