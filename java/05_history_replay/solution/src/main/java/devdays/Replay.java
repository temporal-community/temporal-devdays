package devdays;

import io.temporal.common.WorkflowExecutionHistory;
import io.temporal.testing.WorkflowReplayer;
import java.util.List;

public class Replay {
    public static List<String> eventTypes(String historyJson) {
        // TODO 1 start: return the name of every event in the history, without the EVENT_TYPE_ prefix
        return WorkflowExecutionHistory.fromJson(historyJson).getEvents().stream()
                .map(e -> e.getEventType().name().replace("EVENT_TYPE_", ""))
                .toList();
        // TODO 1 end
    }

    public static void replay(String historyJson, Class<?> workflowImpl) throws Exception {
        // TODO 2 start: replay the history against the workflow code, it throws when the code no longer matches
        WorkflowReplayer.replayWorkflowExecution(historyJson, workflowImpl);
        // TODO 2 end
    }
}
