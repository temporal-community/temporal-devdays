package devdays;

import io.temporal.workflow.Workflow;

public class TransferWorkflowImpl implements TransferWorkflow {
    private boolean approved = false;

    @Override
    public String run() {
        Workflow.await(() -> approved);
        return "approved";
    }

    @Override
    public void approve() {
        approved = true;
    }
}
