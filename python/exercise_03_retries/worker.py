import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import charge
from workflows import PaymentWorkflow

TASK_QUEUE = "payments"


def make_worker(client: Client) -> Worker:
    return Worker(
        client, task_queue=TASK_QUEUE, workflows=[PaymentWorkflow], activities=[charge])


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
