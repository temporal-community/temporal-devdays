package devdays;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.UpdateMethod;
import io.temporal.workflow.UpdateValidatorMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TransferWorkflow {
    @WorkflowMethod
    String run();

    @SignalMethod(name = "approve")
    void approve();

    @QueryMethod(name = "get_status")
    String getStatus();

    @UpdateMethod(name = "set_amount")
    int setAmount(int amount);

    @UpdateValidatorMethod(updateName = "set_amount")
    void checkAmount(int amount);
}
