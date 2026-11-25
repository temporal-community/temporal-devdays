package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface FraudActivities {
    void checkFraud(TransferRequest req);
}
