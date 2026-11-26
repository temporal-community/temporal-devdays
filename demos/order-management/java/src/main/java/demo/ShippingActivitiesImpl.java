package demo;

import io.temporal.failure.ApplicationFailure;

public class ShippingActivitiesImpl implements ShippingActivities {
    @Override
    public void shipItem(String orderId, String item, String scenario) {
        if (scenario.equals("ship_fails") && item.equals("keypad")) {
            Calls.all.add("ship_failed:" + item);
            throw ApplicationFailure.newNonRetryableFailure("no courier available", "NoCourier");
        }
        Calls.all.add("ship:" + item);
    }
}
