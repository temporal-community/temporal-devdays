from pathlib import Path

from temporalio.client import WorkflowHistory
from temporalio.worker import Replayer

from workflows import PizzaWorkflow


async def test_the_recorded_history_still_replays():
    # TODO 4 start: load history.json with WorkflowHistory.from_json and replay it with a Replayer for PizzaWorkflow
    raise NotImplementedError("TODO 4: load history.json with WorkflowHistory.from_json and replay it with a Replayer for PizzaWorkflow")
    # TODO 4 end
