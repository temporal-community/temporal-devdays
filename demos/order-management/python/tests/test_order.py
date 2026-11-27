import asyncio
from contextlib import AsyncExitStack

import activities
import pytest
from models import OrderInput
from temporalio.client import WorkflowUpdateFailedError
from temporalio.testing import WorkflowEnvironment
from worker import TASK_QUEUE, make_workers
from workflows import OrderWorkflow


@pytest.fixture
async def env():
    activities.calls.clear()
    activities.charged.clear()
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with AsyncExitStack() as stack:
            for worker in make_workers(env.client):
                await stack.enter_async_context(worker)
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
    assert "refund_charge" not in activities.calls and "release_stock" not in activities.calls


async def test_flaky_card_succeeds_on_the_third_attempt(env):
    result = await (await start(env, "flaky_card")).result()
    assert result.status == "shipped"
    assert activities.calls.count("charge") == 3


async def test_bad_card_fails_fast_and_releases_the_stock(env):
    result = await (await start(env, "bad_card")).result()
    assert result.status == "cancelled: card declined"
    assert activities.calls.count("charge") == 1
    assert "release_stock" in activities.calls
    assert "refund_charge" not in activities.calls, "a declined card moved no money"
    assert "refund_skipped" in activities.calls, "the refund still runs, finds no charge and does nothing"
    assert activities.calls.index("refund_skipped") < activities.calls.index("release_stock"), "undo runs in reverse order"
    assert not any(c.startswith("ship:") for c in activities.calls)


async def test_shipping_failure_refunds_the_card_then_releases_the_stock(env):
    result = await (await start(env, "ship_fails")).result()
    assert result.status == "cancelled: shipping failed"
    calls = activities.calls
    assert calls.count("charge") == 1
    assert calls.count("refund_charge") == 1 and calls.count("release_stock") == 1
    assert calls.index("refund_charge") < calls.index("release_stock"), "undo steps run in reverse order"
    assert calls.index("charge") < calls.index("refund_charge")
    child = env.client.get_workflow_handle("ship-1")
    assert (await child.describe()).status.name == "FAILED"


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
    assert "charge" not in activities.calls and "refund_charge" not in activities.calls


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
