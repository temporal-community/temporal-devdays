package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class PizzaWorkflowImpl implements PizzaWorkflow {
    private final PizzaActivities activities = Workflow.newActivityStub(
            PizzaActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public String run(PizzaOrder order) {
        int total = order.items().stream().mapToInt(Pizza::price).sum();
        int distance = activities.getDistance(order.address());
        if (order.delivery() && distance > 25) {
            throw ApplicationFailure.newFailure("too far", "TooFar");
        }
        Workflow.sleep(Duration.ofSeconds(order.prepSeconds()));
        return activities.sendBill(new Bill(order.customer().id(), total));
    }
}
