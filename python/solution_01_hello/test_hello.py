import asyncio

from temporalio.testing import WorkflowEnvironment

from worker import TASK_QUEUE, make_worker
from workflows import GreetSomeone


async def test_greets():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            result = await asyncio.wait_for(
                env.client.execute_workflow(
                    GreetSomeone.run, "Temporal", id="t1", task_queue=TASK_QUEUE),
                20,
            )
    assert result == "Hello, Temporal!"
