import asyncio

from temporalio.api.enums.v1 import EventType
from temporalio.testing import WorkflowEnvironment

from worker import TASK_QUEUE, make_worker
from workflows import OrderWorkflow


async def test_waits_on_a_timer_then_finishes():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                OrderWorkflow.run, "Margherita", id="t1", task_queue=TASK_QUEUE)
            result = await asyncio.wait_for(handle.result(), 30)
            history = await handle.fetch_history()
    types = [e.event_type for e in history.events]
    assert result == "baked ticket-Margherita"
    assert EventType.EVENT_TYPE_TIMER_STARTED in types, "no Timer in the history: TODO 1 is not done"
