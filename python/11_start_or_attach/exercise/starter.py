import asyncio

from temporalio.client import Client
from temporalio.exceptions import WorkflowAlreadyStartedError

from orders import TASK_QUEUE, add_item, start_transfer
from workflows import OrderWorkflow, TransferWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    transfer = await start_transfer(client, "42")
    try:
        await start_transfer(client, "42")
    except WorkflowAlreadyStartedError:
        print("second start rejected")
    await transfer.signal(TransferWorkflow.approve)
    print("transfer:", await transfer.result())
    await add_item(client, "7", "pizza")
    order = await add_item(client, "7", "cola")
    await order.signal(OrderWorkflow.close)
    print("order items:", await order.result())


if __name__ == "__main__":
    asyncio.run(main())
