from temporalio import activity

from llm import mock_llm
from models import Decision, Message


@activity.defn
async def llm_decide(messages: list[Message]) -> Decision:
    if activity.info().attempt == 1 and len(messages) == 1:
        raise Exception("rate limited")
    return mock_llm(messages)


@activity.defn
async def run_tool(decision: Decision) -> str:
    return "sunny" if decision.tool == "get_weather" else "unknown tool"
