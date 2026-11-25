package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class GreetSomeoneImpl implements GreetSomeone {
    private final GreetingActivities activities = Workflow.newActivityStub(
            GreetingActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public String run(String name) {
        // TODO 2 start [workflow]: call activities.createGreeting(name) and return the result
        throw io.temporal.failure.ApplicationFailure.newNonRetryableFailure("TODO 2: call activities.createGreeting(name) and return the result", "Todo");
        // TODO 2 end
    }
}
