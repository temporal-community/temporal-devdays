import asyncio

import pytest
from temporalio.client import WorkflowUpdateFailedError
from temporalio.testing import WorkflowEnvironment

from worker import TASK_QUEUE, make_worker
from workflows import TransferWorkflow


async def test_signal_query_and_update():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                TransferWorkflow.run, id="t1", task_queue=TASK_QUEUE)
            assert await handle.query(TransferWorkflow.get_status) == "waiting"
            assert await handle.execute_update(TransferWorkflow.set_amount, 150) == 150
            with pytest.raises(WorkflowUpdateFailedError):
                await handle.execute_update(TransferWorkflow.set_amount, -5)
            await handle.signal(TransferWorkflow.approve)
            result = await asyncio.wait_for(handle.result(), 20)
    assert result == "sent 150"
