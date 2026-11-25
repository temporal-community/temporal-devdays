package devdays;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GreetSomeone {
    @WorkflowMethod
    String run(String name);
}
