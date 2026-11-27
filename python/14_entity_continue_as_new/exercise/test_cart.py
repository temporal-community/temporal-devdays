import asyncio

from temporalio.testing import WorkflowEnvironment

from worker import TASK_QUEUE, make_worker
from workflows import CartState, CartWorkflow


async def wait_for_new_run(client, workflow_id: str, old_run_id: str) -> None:
    handle = client.get_workflow_handle(workflow_id)
    for _ in range(100):
        if (await handle.describe()).run_id != old_run_id:
            return
        await asyncio.sleep(0.1)
    raise AssertionError("TODO 1: the Workflow never continued as new after 10 items")


async def test_the_cart_keeps_every_item_across_runs():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                CartWorkflow.run, args=[CartState(), 10], id="cart-1", task_queue=TASK_QUEUE)
            first_run = (await env.client.get_workflow_handle("cart-1").describe()).run_id
            for i in range(25):
                await handle.signal(CartWorkflow.add_item, f"item-{i}")
                if i == 9:
                    await wait_for_new_run(env.client, "cart-1", first_run)
            await handle.signal(CartWorkflow.close)
            result = await asyncio.wait_for(handle.result(), 30)
    assert result.items == [f"item-{i}" for i in range(25)]
    assert result.runs >= 2, "TODO 1: continue as new after max_per_run items, carrying the items forward"


async def test_a_signal_that_arrives_with_the_start_is_not_lost():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                CartWorkflow.run, args=[CartState(["a"], 2), 10], id="cart-2", task_queue=TASK_QUEUE,
                start_signal="add_item", start_signal_args=["b"])
            await handle.signal(CartWorkflow.close)
            result = await asyncio.wait_for(handle.result(), 30)
    assert result.items == ["a", "b"], "messages that arrive before the run starts must be kept"
