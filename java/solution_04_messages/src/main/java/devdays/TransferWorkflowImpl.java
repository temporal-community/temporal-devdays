package devdays;

import io.temporal.workflow.Workflow;

public class TransferWorkflowImpl implements TransferWorkflow {
    private boolean approved = false;
    private int amount = 100;
    private String status = "waiting";

    @Override
    public String run() {
        Workflow.await(() -> approved);
        status = "sent";
        return "sent " + amount;
    }

    @Override
    public void approve() {
        // TODO 1 start [workflow]: set approved to true
        approved = true;
        // TODO 1 end
    }

    @Override
    public String getStatus() {
        // TODO 2 start: return status
        return status;
        // TODO 2 end
    }

    @Override
    public int setAmount(int amount) {
        // TODO 3 start [workflow]: store amount in this.amount and return it
        this.amount = amount;
        return this.amount;
        // TODO 3 end
    }

    @Override
    public void checkAmount(int amount) {
        // TODO 4 start [workflow]: throw IllegalArgumentException when amount is not positive
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        // TODO 4 end
    }
}
