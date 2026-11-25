package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface NotifyActivities {
    void sendReceipt(TransferRequest req);
}
