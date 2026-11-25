package devdays;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ApprovalWorkflow {
    @WorkflowMethod
    String run(int amount);

    @SignalMethod
    void approve(String approver);
}
