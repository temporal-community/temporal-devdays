package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ChargeActivities {
    String charge(String card);
}
