package devdays;

import io.temporal.testing.WorkflowReplayer;
import org.junit.jupiter.api.Test;

class PizzaReplayTest {
    @Test
    void theRecordedHistoryStillReplays() throws Exception {
        // TODO 4 start: replay history.json against PizzaWorkflowImpl with WorkflowReplayer
        WorkflowReplayer.replayWorkflowExecutionFromResource("history.json", PizzaWorkflowImpl.class);
        // TODO 4 end
    }
}
