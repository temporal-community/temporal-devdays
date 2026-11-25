package devdays;

import io.temporal.common.WorkflowExecutionHistory;
import io.temporal.testing.WorkflowReplayer;
import java.util.List;

public class Replay {
    public static List<String> eventTypes(String historyJson) {
        // TODO 1 start: return the name of every event in the history, without the EVENT_TYPE_ prefix
        throw new UnsupportedOperationException("TODO 1: return the name of every event in the history, without the EVENT_TYPE_ prefix");
        // TODO 1 end
    }

    public static void replay(String historyJson, Class<?> workflowImpl) throws Exception {
        // TODO 2 start: replay the history against the workflow code, it throws when the code no longer matches
        throw new UnsupportedOperationException("TODO 2: replay the history against the workflow code, it throws when the code no longer matches");
        // TODO 2 end
    }
}
