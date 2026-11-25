import asyncio

import pytest
from temporalio.exceptions import WorkflowAlreadyStartedError
from temporalio.testing import WorkflowEnvironment

from orders import add_item, start_transfer
from worker import make_worker
from workflows import OrderWorkflow


async def test_a_second_start_of_the_same_transfer_is_rejected():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            first = await start_transfer(env.client, "t1")
            assert first.id == "transfer-t1", "TODO 1: use the business id as the Workflow ID"
            with pytest.raises(WorkflowAlreadyStartedError):
                await start_transfer(env.client, "t1")
                pytest.fail("TODO 1: a second start for the same transfer must be rejected")


async def test_add_item_starts_or_attaches():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            try:
                await add_item(env.client, "o1", "pizza")
                handle = await add_item(env.client, "o1", "cola")
            except WorkflowAlreadyStartedError as err:
                pytest.fail(f"TODO 2: the second call failed ({err}), use signal-with-start so it starts or attaches")
            await handle.signal(OrderWorkflow.close)
            assert await asyncio.wait_for(handle.result(), 20) == ["pizza", "cola"]
