package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface PizzaActivities {
    int getDistance(String address);

    String sendBill(Bill bill);
}
