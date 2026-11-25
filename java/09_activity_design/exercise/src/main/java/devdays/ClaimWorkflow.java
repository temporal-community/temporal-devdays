package devdays;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ClaimWorkflow {
    @WorkflowMethod
    String run(Claim claim);
}
