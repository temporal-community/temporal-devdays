package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class PaymentWorkflowImpl implements PaymentWorkflow {
    @Override
    public String run(String card) {
        // TODO 1 start [workflow]: build the activity stub with RetryOptions: initial interval 10 ms, maximum 5 attempts, then call charge
        ChargeActivities activities = Workflow.newActivityStub(
                ChargeActivities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(5))
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setInitialInterval(Duration.ofMillis(10))
                                .setMaximumAttempts(5)
                                .build())
                        .build());
        return activities.charge(card);
        // TODO 1 end
    }
}
