package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class ClaimWorkflowImpl implements ClaimWorkflow {
    private final ClaimActivities activities = Workflow.newActivityStub(
            ClaimActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public String run(Claim claim) {
        // TODO 1 start [workflow]: decide with plain code in the Workflow, no Activity needed
        String decision = activities.evaluateClaim(claim);
        // TODO 1 end
        return decision;
    }
}
