package devdays;

import io.temporal.activity.Activity;
import java.util.List;

public class AgentActivitiesImpl implements AgentActivities {
    @Override
    public Decision llmDecide(List<Message> messages) {
        if (Activity.getExecutionContext().getInfo().getAttempt() == 1 && messages.size() == 1) {
            throw new RuntimeException("rate limited");
        }
        return MockLlm.decide(messages);
    }

    @Override
    public String runTool(Decision decision) {
        return decision.tool().equals("get_weather") ? "sunny" : "unknown tool";
    }
}
