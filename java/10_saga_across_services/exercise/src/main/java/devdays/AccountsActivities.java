package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface AccountsActivities {
    void withdraw(TransferRequest req);

    void deposit(TransferRequest req);

    void undoWithdraw(TransferRequest req);
}
