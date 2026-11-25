package devdays;

import io.temporal.workflow.Workflow;
import java.time.Duration;

public class ApprovalWorkflowImpl implements ApprovalWorkflow {
    private String approver = null;

    @Override
    public String run(int amount) {
        // TODO 1 start [workflow]: wait until approver is set with Workflow.await and a 24 hour timeout, return "timed out" on timeout
        if (true) throw io.temporal.failure.ApplicationFailure.newNonRetryableFailure("TODO 1: wait until approver is set with Workflow.await and a 24 hour timeout, return \"timed out\" on timeout", "Todo");
        // TODO 1 end
        return "approved by " + approver;
    }

    @Override
    public void approve(String approver) {
        // TODO 2 start [workflow]: remember who approved
        throw io.temporal.failure.ApplicationFailure.newNonRetryableFailure("TODO 2: remember who approved", "Todo");
        // TODO 2 end
    }
}
