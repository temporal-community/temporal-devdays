package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class OrderWorkflowImpl implements OrderWorkflow {
    private final OrderActivities activities = Workflow.newActivityStub(
            OrderActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public String run(String name, long waitSeconds) {
        String ticket = activities.takeOrder(name);
        // TODO 1 start [workflow]: wait waitSeconds with Workflow.sleep, this is a durable Timer
        Workflow.sleep(Duration.ofSeconds(waitSeconds));
        // TODO 1 end
        return activities.bake(ticket);
    }
}
