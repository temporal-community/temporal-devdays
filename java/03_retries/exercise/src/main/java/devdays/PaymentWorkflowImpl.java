package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class PaymentWorkflowImpl implements PaymentWorkflow {
    @Override
    public String run(String card) {
        // TODO 1 start [workflow]: build the activity stub with RetryOptions: initial interval 10 ms, maximum 5 attempts, then call charge
        throw io.temporal.failure.ApplicationFailure.newNonRetryableFailure("TODO 1: build the activity stub with RetryOptions: initial interval 10 ms, maximum 5 attempts, then call charge", "Todo");
        // TODO 1 end
    }
}
