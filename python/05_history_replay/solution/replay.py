from temporalio.api.enums.v1 import EventType
from temporalio.client import WorkflowHistory
from temporalio.worker import Replayer

from workflows import PizzaWorkflow


def event_types(history_json: str) -> list[str]:
    # TODO 1 start: return the name of every event in the history, without the EVENT_TYPE_ prefix
    history = WorkflowHistory.from_json("order-1", history_json)
    return [EventType.Name(e.event_type).removeprefix("EVENT_TYPE_") for e in history.events]
    # TODO 1 end


async def replay_history(history_json: str, workflow=PizzaWorkflow) -> None:
    # TODO 2 start: replay the history against the workflow code, it raises when the code no longer matches
    history = WorkflowHistory.from_json("order-1", history_json)
    await Replayer(workflows=[workflow]).replay_workflow(history)
    # TODO 2 end
