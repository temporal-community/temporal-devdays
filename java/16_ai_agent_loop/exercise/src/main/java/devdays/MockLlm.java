package devdays;

import java.util.List;

/** A fake model: ask for the weather first, then answer with what the tool said. */
public class MockLlm {
    public static Decision decide(List<Message> messages) {
        List<String> toolResults = messages.stream()
                .filter(m -> m.role().equals("tool"))
                .map(Message::content)
                .toList();
        if (toolResults.isEmpty()) {
            return new Decision("get_weather", "Pune", "");
        }
        return new Decision("", "", "It is " + toolResults.get(toolResults.size() - 1) + " in Pune");
    }
}
