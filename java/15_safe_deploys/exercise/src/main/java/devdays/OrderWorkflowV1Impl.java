package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

/** The first version of the Workflow, the one that was running before the change. */
public class OrderWorkflowV1Impl implements OrderWorkflow {
    private final OrderActivities activities = Workflow.newActivityStub(
            OrderActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public String run(String name) {
        String ticket = activities.takeOrder(name);
        return activities.bake(ticket);
    }
}
