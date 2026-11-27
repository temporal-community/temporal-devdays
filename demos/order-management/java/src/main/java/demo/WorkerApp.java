package demo;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import java.util.List;
import java.util.function.Function;

public class WorkerApp {
    public static final String TASK_QUEUE = "orders";
    static final List<String> SERVICES = List.of("orders", "inventory", "payments", "shipping");

    public static void register(Function<String, Worker> newWorker, List<String> names) {
        if (names.contains("orders")) {
            newWorker.apply("orders").registerWorkflowImplementationTypes(OrderWorkflowImpl.class, ShipmentWorkflowImpl.class);
        }
        if (names.contains("inventory")) {
            newWorker.apply("inventory").registerActivitiesImplementations(new InventoryActivitiesImpl());
        }
        if (names.contains("payments")) {
            newWorker.apply("payments").registerActivitiesImplementations(new PaymentActivitiesImpl());
        }
        if (names.contains("shipping")) {
            newWorker.apply("shipping").registerActivitiesImplementations(new ShippingActivitiesImpl());
        }
    }

    public static void main(String[] args) {
        WorkflowClient client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        WorkerFactory factory = WorkerFactory.newInstance(client);
        register(queue -> factory.newWorker(queue, WorkerOptions.newBuilder().setIdentity("java-" + queue).build()),
            args.length == 0 ? SERVICES : List.of(args));
        factory.start();
    }
}
