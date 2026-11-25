package devdays;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface OrderWorkflow {
    @WorkflowMethod
    List<String> run();

    @SignalMethod
    void addItem(String item);

    @SignalMethod
    void close();
}
