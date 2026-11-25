package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class OnboardWorkflowImpl implements OnboardWorkflow {
    private final OnboardActivities activities = Workflow.newActivityStub(
            OnboardActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(5))
                    .setRetryOptions(RetryOptions.newBuilder().setInitialInterval(Duration.ofMillis(10)).build())
                    .build());

    @Override
    public String run(String name) {
        // TODO 2 start [workflow]: call addUser, then sendWelcome, as two separate Activities
        activities.addUser(name);
        activities.sendWelcome(name);
        //? activities.createUser(name);
        // TODO 2 end
        return "welcome";
    }
}
