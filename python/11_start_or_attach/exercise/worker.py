import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from orders import TASK_QUEUE
from workflows import OrderWorkflow, TransferWorkflow


def make_worker(client: Client) -> Worker:
    return Worker(client, task_queue=TASK_QUEUE, workflows=[TransferWorkflow, OrderWorkflow])


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
