package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReplayTest {
    private static String history() throws IOException {
        try (var in = ReplayTest.class.getResourceAsStream("/history.json")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void readsTheHistory() throws IOException {
        List<String> types = Replay.eventTypes(history());
        assertEquals("WORKFLOW_EXECUTION_STARTED", types.get(0));
        assertTrue(types.contains("TIMER_STARTED"));
        assertEquals("WORKFLOW_EXECUTION_COMPLETED", types.get(types.size() - 1));
    }

    @Test
    void theSameCodeReplays() throws Exception {
        Replay.replay(history(), PizzaWorkflowImpl.class);
    }

    @Test
    void changedCodeFailsToReplay() throws IOException {
        String history = history();
        assertThrows(Exception.class, () -> Replay.replay(history, SwappedPizzaWorkflowImpl.class));
    }
}
