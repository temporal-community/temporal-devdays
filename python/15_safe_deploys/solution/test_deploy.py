import asyncio
from pathlib import Path

import pytest
from temporalio.client import WorkflowHistory
from temporalio.testing import WorkflowEnvironment
from temporalio.worker import Replayer

from worker import TASK_QUEUE, make_worker
from workflows import OrderWorkflow

HISTORY = (Path(__file__).parent / "history_v1.json").read_text()


async def test_runs_recorded_by_the_old_code_still_replay():
    history = WorkflowHistory.from_json("order-1", HISTORY)
    try:
        await Replayer(workflows=[OrderWorkflow]).replay_workflow(history)
    except Exception as err:
        pytest.fail(f"TODO 1: the new code no longer matches the old history ({type(err).__name__}), "
                    "guard the new step with workflow.patched")


async def test_new_runs_check_the_stock():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                OrderWorkflow.run, "Margherita", id="t1", task_queue=TASK_QUEUE)
            assert await asyncio.wait_for(handle.result(), 20) == "baked ticket-Margherita"
            history = await handle.fetch_history()
    names = [
        e.activity_task_scheduled_event_attributes.activity_type.name
        for e in history.events if e.HasField("activity_task_scheduled_event_attributes")
    ]
    assert names == ["take_order", "check_stock", "bake"]
