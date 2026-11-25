package devdays;

import io.temporal.activity.ActivityInterface;
import java.util.List;

@ActivityInterface
public interface AgentActivities {
    Decision llmDecide(List<Message> messages);

    String runTool(Decision decision);
}
