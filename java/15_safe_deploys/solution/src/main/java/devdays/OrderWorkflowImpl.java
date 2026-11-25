package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class OrderWorkflowImpl implements OrderWorkflow {
    private final OrderActivities activities = Workflow.newActivityStub(
            OrderActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public String run(String name) {
        String ticket = activities.takeOrder(name);
        // TODO 1 start [workflow]: run checkStock only when Workflow.getVersion("check-stock", Workflow.DEFAULT_VERSION, 1) returns 1, so runs recorded by the old code still replay
        if (Workflow.getVersion("check-stock", Workflow.DEFAULT_VERSION, 1) == 1) {
            activities.checkStock(ticket);
        }
        //? activities.checkStock(ticket);
        // TODO 1 end
        return activities.bake(ticket);
    }
}
