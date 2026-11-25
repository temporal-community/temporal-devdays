package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class TransferWorkflowImpl implements TransferWorkflow {
    private static ActivityOptions to(String taskQueue) {
        return ActivityOptions.newBuilder()
                .setTaskQueue(taskQueue)
                .setStartToCloseTimeout(Duration.ofSeconds(5))
                .build();
    }

    private final FraudActivities fraud = Workflow.newActivityStub(FraudActivities.class, to("fraud"));
    private final AccountsActivities accounts = Workflow.newActivityStub(AccountsActivities.class, to("accounts"));
    private final NotifyActivities notify = Workflow.newActivityStub(NotifyActivities.class, to("notifications"));

    @Override
    public String run(TransferRequest req) {
        Saga saga = new Saga(new Saga.Options.Builder().setContinueWithError(true).build());
        try {
            fraud.checkFraud(req);
            // TODO 1 start [workflow]: register the undo with saga.addCompensation BEFORE calling withdraw
            accounts.withdraw(req);
            saga.addCompensation(accounts::undoWithdraw, req);
            // TODO 1 end
            accounts.deposit(req);
            notify.sendReceipt(req);
            return "Transfer completed";
        } catch (ActivityFailure e) {
            // TODO 2 start [workflow]: run the undo steps with saga.compensate()
            // the undo steps never run
            // TODO 2 end
            return "Transfer failed, money returned";
        }
    }
}
