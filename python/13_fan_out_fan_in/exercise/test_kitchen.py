import asyncio

import activities
from temporalio.testing import WorkflowEnvironment
from worker import TASK_QUEUE, make_worker
from workflows import KitchenWorkflow


async def test_items_are_made_in_parallel_and_shipped_by_a_child():
    activities.stats.update(running=0, max_running=0)
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                KitchenWorkflow.run, args=["o1", ["a", "b", "c"]], id="t1", task_queue=TASK_QUEUE)
            result = await asyncio.wait_for(handle.result(), 30)
            history = await handle.fetch_history()
    assert result == ["a ready", "b ready", "c ready", "shipped o1"]
    assert activities.stats["max_running"] > 1, "TODO 1: start all the Activities first, then wait for them together"
    children = [
        e.child_workflow_execution_started_event_attributes.workflow_execution.workflow_id
        for e in history.events
        if e.HasField("child_workflow_execution_started_event_attributes")
    ]
    assert children == ["ship-o1"], "TODO 2: ship the order in a child Workflow with the id ship-o1"
