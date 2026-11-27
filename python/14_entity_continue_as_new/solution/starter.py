import asyncio

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import CartState, CartWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    await client.start_workflow(
        CartWorkflow.run, args=[CartState(), 3], id="cart-1", task_queue=TASK_QUEUE)
    cart = client.get_workflow_handle("cart-1")
    for i in range(7):
        await cart.signal(CartWorkflow.add_item, f"item-{i}")
        await asyncio.sleep(0.3)
    await cart.signal(CartWorkflow.close)
    state = await cart.result()
    print(f"cart has {len(state['items'])} items after {state['runs']} runs")


if __name__ == "__main__":
    asyncio.run(main())
