package devdays;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TransferWorkflow {
    @WorkflowMethod
    String run();

    @SignalMethod
    void approve();
}
