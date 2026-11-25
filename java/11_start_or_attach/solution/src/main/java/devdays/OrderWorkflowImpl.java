package devdays;

import io.temporal.workflow.Workflow;
import java.util.ArrayList;
import java.util.List;

public class OrderWorkflowImpl implements OrderWorkflow {
    private final List<String> items = new ArrayList<>();
    private boolean closed = false;

    @Override
    public List<String> run() {
        Workflow.await(() -> closed);
        return items;
    }

    @Override
    public void addItem(String item) {
        items.add(item);
    }

    @Override
    public void close() {
        closed = true;
    }
}
