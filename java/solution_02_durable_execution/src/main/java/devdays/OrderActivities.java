package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivities {
    String takeOrder(String name);

    String bake(String ticket);
}
