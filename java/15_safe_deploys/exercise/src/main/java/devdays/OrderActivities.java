package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivities {
    String takeOrder(String name);

    String checkStock(String ticket);

    String bake(String ticket);
}
