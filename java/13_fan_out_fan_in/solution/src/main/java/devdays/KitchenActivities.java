package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface KitchenActivities {
    String makeItem(String item);
}
