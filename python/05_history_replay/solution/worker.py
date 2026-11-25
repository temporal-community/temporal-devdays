import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import get_distance, send_bill
from workflows import PizzaWorkflow

TASK_QUEUE = "pizza"


def make_worker(client: Client) -> Worker:
    return Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[PizzaWorkflow],
        activities=[get_distance, send_bill],
    )


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
