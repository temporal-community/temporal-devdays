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
        throw io.temporal.failure.ApplicationFailure.newNonRetryableFailure("TODO 1: set approved to true", "Todo");
        // TODO 1 end
    }

    @Override
    public String getStatus() {
        // TODO 2 start: return status
        throw new UnsupportedOperationException("TODO 2: return status");
        // TODO 2 end
    }

    @Override
    public int setAmount(int amount) {
        // TODO 3 start [workflow]: store amount in this.amount and return it
        throw io.temporal.failure.ApplicationFailure.newNonRetryableFailure("TODO 3: store amount in this.amount and return it", "Todo");
        // TODO 3 end
    }

    @Override
    public void checkAmount(int amount) {
        // TODO 4 start [workflow]: throw IllegalArgumentException when amount is not positive
        throw io.temporal.failure.ApplicationFailure.newNonRetryableFailure("TODO 4: throw IllegalArgumentException when amount is not positive", "Todo");
        // TODO 4 end
    }
}
