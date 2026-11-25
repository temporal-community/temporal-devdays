import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import make_item
from workflows import KitchenWorkflow, ShipmentWorkflow

TASK_QUEUE = "kitchen"


def make_worker(client: Client) -> Worker:
    return Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[KitchenWorkflow, ShipmentWorkflow],
        activities=[make_item],
    )


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
