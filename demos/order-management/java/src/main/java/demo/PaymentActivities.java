package demo;

import demo.Models.OrderInput;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PaymentActivities {
    @ActivityMethod(name = "charge")
    void charge(OrderInput order);

    @ActivityMethod(name = "refund_charge")
    void refundCharge(OrderInput order);
}
