package devdays;

import io.temporal.workflow.Workflow;
import java.util.ArrayList;
import java.util.List;

public class CartWorkflowImpl implements CartWorkflow {
    private final List<String> items = new ArrayList<>();
    private int runs = 1;
    private int added = 0;
    private boolean closed = false;

    @Override
    public CartState run(CartState state, int maxPerRun) {
        items.addAll(0, state.items());   // signals can arrive before run starts
        runs = state.runs();
        Workflow.await(() -> added >= maxPerRun || closed);
        if (closed) {
            return new CartState(items, runs);
        }
        // TODO 1 start [workflow]: continue as new with the items so far and runs + 1, so the history stays small
        Workflow.await(() -> closed);
        // TODO 1 end
        return new CartState(items, runs);
    }

    @Override
    public void addItem(String item) {
        items.add(item);
        added++;
    }

    @Override
    public void close() {
        closed = true;
    }
}
