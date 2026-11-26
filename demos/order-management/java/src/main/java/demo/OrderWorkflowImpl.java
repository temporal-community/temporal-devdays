package demo;

import demo.Models.OrderInput;
import demo.Models.OrderOutput;
import demo.Models.OrderStatus;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OrderWorkflowImpl implements OrderWorkflow {
    private final Activities activities = Workflow.newActivityStub(Activities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(RetryOptions.newBuilder().setInitialInterval(Duration.ofSeconds(1)).setMaximumAttempts(5).build())
            .build());

    private String step = "starting";
    private String address = "";
    private boolean approved = false;

    @Override
    public OrderOutput run(OrderInput order) {
        address = order.address();
        step = "checking stock";
        List<String> items = activities.checkStock(order);

        step = "reserving stock";
        List<Consumer<OrderInput>> undo = new ArrayList<>(List.of(activities::releaseStock));  // registered before the step
        activities.reserveStock(order);

        if (order.scenario().equals("approval")) {
            step = "waiting for approval";
            if (!Workflow.await(Duration.ofHours(1), () -> approved)) {
                return cancel(order, undo, "approval timed out");
            }
        }

        step = "charging card";
        try {
            activities.charge(order);
        } catch (ActivityFailure e) {
            return cancel(order, undo, "card declined");
        }

        step = "shipping";
        ShipmentWorkflow shipment = Workflow.newChildWorkflowStub(ShipmentWorkflow.class,
            ChildWorkflowOptions.newBuilder().setWorkflowId("ship-" + order.orderId()).build());
        String trackingId = shipment.run(order.orderId(), address, items);
        step = "done";
        return new OrderOutput("shipped", trackingId, address);
    }

    private OrderOutput cancel(OrderInput order, List<Consumer<OrderInput>> undo, String reason) {
        step = "cancelling";
        for (int i = undo.size() - 1; i >= 0; i--) {
            undo.get(i).accept(order);
        }
        step = "cancelled";
        return new OrderOutput("cancelled: " + reason, "", address);
    }

    @Override
    public void approve() {
        approved = true;
    }

    @Override
    public String changeAddress(String address) {
        this.address = address;
        return address;
    }

    @Override
    public void checkAddress(String address) {
        if (address.strip().length() < 5) {
            throw new IllegalArgumentException("address is too short");
        }
    }

    @Override
    public OrderStatus status() {
        return new OrderStatus(step, address);
    }
}
