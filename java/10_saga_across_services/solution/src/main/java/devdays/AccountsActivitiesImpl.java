package devdays;

import io.temporal.failure.ApplicationFailure;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountsActivitiesImpl implements AccountsActivities {
    // lets the check see what happened
    public static final List<String> withdrawn = new CopyOnWriteArrayList<>();
    public static final List<String> undone = new CopyOnWriteArrayList<>();

    @Override
    public void withdraw(TransferRequest req) {
        withdrawn.add(req.fromAccount());
        if (req.failAt().equals("withdraw")) {
            throw ApplicationFailure.newNonRetryableFailure("withdraw failed after it landed", "WithdrawFailed");
        }
    }

    @Override
    public void deposit(TransferRequest req) {
        if (req.failAt().equals("deposit")) {
            throw ApplicationFailure.newNonRetryableFailure("deposit failed", "DepositFailed");
        }
    }

    @Override
    public void undoWithdraw(TransferRequest req) {
        undone.add(req.fromAccount());
    }
}
