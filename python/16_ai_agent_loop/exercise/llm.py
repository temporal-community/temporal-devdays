from models import Decision, Message


def mock_llm(messages: list[Message]) -> Decision:
    """A fake model: ask for the weather first, then answer with what the tool said."""
    tool_results = [m.content for m in messages if m.role == "tool"]
    if not tool_results:
        return Decision(tool="get_weather", argument="Pune")
    return Decision(answer=f"It is {tool_results[-1]} in Pune")
