package devdays;

import io.temporal.workflow.Workflow;
import java.time.Duration;

public class ApprovalWorkflowImpl implements ApprovalWorkflow {
    private String approver = null;

    @Override
    public String run(int amount) {
        // TODO 1 start [workflow]: wait until approver is set with Workflow.await and a 24 hour timeout, return "timed out" on timeout
        boolean answered = Workflow.await(Duration.ofHours(24), () -> approver != null);
        if (!answered) {
            return "timed out";
        }
        // TODO 1 end
        return "approved by " + approver;
    }

    @Override
    public void approve(String approver) {
        // TODO 2 start [workflow]: remember who approved
        this.approver = approver;
        // TODO 2 end
    }
}
