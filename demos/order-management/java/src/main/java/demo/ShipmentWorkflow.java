package demo;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface ShipmentWorkflow {
    @WorkflowMethod(name = "ShipmentWorkflow")
    String run(String orderId, String address, List<String> items, String scenario);
}
