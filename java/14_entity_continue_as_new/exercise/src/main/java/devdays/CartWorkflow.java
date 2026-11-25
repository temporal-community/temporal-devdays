package devdays;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CartWorkflow {
    @WorkflowMethod
    CartState run(CartState state, int maxPerRun);

    @SignalMethod
    void addItem(String item);

    @SignalMethod
    void close();
}
