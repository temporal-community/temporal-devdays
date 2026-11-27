package devdays;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface RenderWorkflow {
    @WorkflowMethod
    List<String> run(List<String> items);
}
