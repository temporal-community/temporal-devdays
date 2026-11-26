package demo;

public class ShippingActivitiesImpl implements ShippingActivities {
    @Override
    public void shipItem(String orderId, String item) {
        Calls.all.add("ship:" + item);
    }
}
