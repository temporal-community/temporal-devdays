import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import add_user, create_user, evaluate_claim, send_welcome
from workflows import ClaimWorkflow, OnboardWorkflow

TASK_QUEUE = "design"


def make_worker(client: Client) -> Worker:
    return Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[ClaimWorkflow, OnboardWorkflow],
        activities=[evaluate_claim, add_user, send_welcome, create_user],
    )


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
