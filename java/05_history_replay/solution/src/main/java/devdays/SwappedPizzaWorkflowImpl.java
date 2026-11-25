package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

/** The same Workflow with the Timer moved before the distance check. */
public class SwappedPizzaWorkflowImpl implements PizzaWorkflow {
    private final PizzaActivities activities = Workflow.newActivityStub(
            PizzaActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public String run(PizzaOrder order) {
        Workflow.sleep(Duration.ofSeconds(order.prepSeconds()));
        activities.getDistance(order.address());
        int total = order.items().stream().mapToInt(Pizza::price).sum();
        return activities.sendBill(new Bill(order.customer().id(), total));
    }
}
