package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface GreetingActivities {
    String createGreeting(String name);
}
