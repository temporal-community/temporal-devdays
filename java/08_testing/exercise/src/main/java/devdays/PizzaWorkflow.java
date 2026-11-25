package devdays;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PizzaWorkflow {
    @WorkflowMethod
    String run(PizzaOrder order);
}
