import asyncio

from temporalio.testing import WorkflowEnvironment

from worker import TASK_QUEUE, make_worker
from workflows import ApprovalWorkflow


async def test_approved_by_a_person():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                ApprovalWorkflow.run, 500, id="t1", task_queue=TASK_QUEUE)
            await handle.signal(ApprovalWorkflow.approve, "alice")
            assert await asyncio.wait_for(handle.result(), 20) == "approved by alice"


async def test_times_out_when_nobody_answers():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                ApprovalWorkflow.run, 500, id="t2", task_queue=TASK_QUEUE)
            assert await asyncio.wait_for(handle.result(), 20) == "timed out"
