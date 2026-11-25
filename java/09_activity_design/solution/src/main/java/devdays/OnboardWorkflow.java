package devdays;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OnboardWorkflow {
    @WorkflowMethod
    String run(String name);
}
