package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class KitchenWorkflowImpl implements KitchenWorkflow {
    private final KitchenActivities activities = Workflow.newActivityStub(
            KitchenActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(10)).build());

    @Override
    public List<String> run(String orderId, List<String> items) {
        List<String> ready = new ArrayList<>();
        // TODO 1 start [workflow]: start makeItem for every item with Async.function, then wait for all the Promises
        for (String item : items) {
            ready.add(activities.makeItem(item));
        }
        // TODO 1 end
        // TODO 2 start [workflow]: ship the order in a child workflow with the id "ship-" + orderId
        String shipped = "shipped " + orderId;
        // TODO 2 end
        ready.add(shipped);
        return ready;
    }
}
