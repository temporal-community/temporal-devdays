import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import bake, check_stock, take_order
from workflows import OrderWorkflow

TASK_QUEUE = "orders"


def make_worker(client: Client) -> Worker:
    return Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[OrderWorkflow],
        activities=[take_order, check_stock, bake],
    )


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
