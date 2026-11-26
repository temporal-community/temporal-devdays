package demo;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.List;

public class ShipmentWorkflowImpl implements ShipmentWorkflow {
    private final Activities activities = Workflow.newActivityStub(Activities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(RetryOptions.newBuilder().setInitialInterval(Duration.ofSeconds(1)).setMaximumAttempts(5).build())
            .build());

    @Override
    public String run(String orderId, String address, List<String> items) {
        List<Promise<Void>> shipping = items.stream()
            .map(item -> Async.procedure(activities::shipItem, orderId, item))
            .toList();
        Promise.allOf(shipping).get();
        return "TRK-" + orderId;
    }
}
