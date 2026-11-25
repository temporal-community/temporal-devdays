from pathlib import Path

from temporalio.client import WorkflowHistory
from temporalio.worker import Replayer

from workflows import PizzaWorkflow


async def test_the_recorded_history_still_replays():
    # TODO 4 start: load history.json with WorkflowHistory.from_json and replay it with a Replayer for PizzaWorkflow
    text = (Path(__file__).parent / "history.json").read_text()
    history = WorkflowHistory.from_json("order-1", text)
    await Replayer(workflows=[PizzaWorkflow]).replay_workflow(history)
    # TODO 4 end
