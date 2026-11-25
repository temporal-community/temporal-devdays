package devdays;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ShipmentWorkflow {
    @WorkflowMethod
    String run(String orderId);
}
