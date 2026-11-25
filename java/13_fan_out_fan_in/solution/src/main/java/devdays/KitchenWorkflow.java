package devdays;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface KitchenWorkflow {
    @WorkflowMethod
    List<String> run(String orderId, List<String> items);
}
