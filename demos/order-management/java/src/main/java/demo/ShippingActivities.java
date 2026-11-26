package demo;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ShippingActivities {
    @ActivityMethod(name = "ship_item")
    void shipItem(String orderId, String item, String scenario);
}
