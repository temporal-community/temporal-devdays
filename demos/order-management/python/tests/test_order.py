import asyncio

import activities
import pytest
from models import OrderInput
from temporalio.client import WorkflowUpdateFailedError
from temporalio.testing import WorkflowEnvironment
from worker import TASK_QUEUE, make_worker
from workflows import OrderWorkflow


@pytest.fixture
async def env():
    activities.calls.clear()
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            yield env


async def start(env, scenario: str, order_id: str = "1"):
    return await env.client.start_workflow(
        OrderWorkflow.run,
        OrderInput(order_id, "1 Main St, Redwood", scenario),
        id=f"order-{order_id}",
        task_queue=TASK_QUEUE,
    )


async def wait_for_step(handle, step: str):
    for _ in range(100):
        if (await handle.query(OrderWorkflow.status)).step == step:
            return
        await asyncio.sleep(0.05)
    raise AssertionError(f"never reached step: {step}")


async def test_happy_path_ships_every_item_with_a_child_workflow(env):
    result = await (await start(env, "happy")).result()
    assert result.status == "shipped"
    assert result.tracking_id == "TRK-1"
    assert sorted(c for c in activities.calls if c.startswith("ship:")) == [
        "ship:keypad", "ship:table legs", "ship:table top"]
    child = env.client.get_workflow_handle("ship-1")
    assert (await child.describe()).status.name == "COMPLETED"


async def test_flaky_card_succeeds_on_the_third_attempt(env):
    result = await (await start(env, "flaky_card")).result()
    assert result.status == "shipped"
    assert activities.calls.count("charge") == 3


async def test_bad_card_fails_fast_and_releases_the_stock(env):
    result = await (await start(env, "bad_card")).result()
    assert result.status == "cancelled: card declined"
    assert activities.calls.count("charge") == 1
    assert "release_stock" in activities.calls
    assert not any(c.startswith("ship:") for c in activities.calls)


async def test_approval_waits_for_the_signal(env):
    handle = await start(env, "approval")
    await wait_for_step(handle, "waiting for approval")
    assert "charge" not in activities.calls
    await handle.signal(OrderWorkflow.approve)
    assert (await handle.result()).status == "shipped"


async def test_unapproved_order_is_cancelled_after_an_hour(env):
    result = await (await start(env, "approval")).result()
    assert result.status == "cancelled: approval timed out"
    assert "release_stock" in activities.calls


async def test_update_changes_the_shipping_address(env):
    handle = await start(env, "approval")
    await wait_for_step(handle, "waiting for approval")
    await handle.execute_update(OrderWorkflow.change_address, "9 New Road, Austin")
    await handle.signal(OrderWorkflow.approve)
    assert (await handle.result()).address == "9 New Road, Austin"


async def test_update_with_a_bad_address_is_rejected(env):
    handle = await start(env, "approval")
    await wait_for_step(handle, "waiting for approval")
    with pytest.raises(WorkflowUpdateFailedError):
        await handle.execute_update(OrderWorkflow.change_address, "x")
    assert (await handle.query(OrderWorkflow.status)).address == "1 Main St, Redwood"
