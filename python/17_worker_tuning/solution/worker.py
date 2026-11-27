import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import render
from tuning import max_per_second, max_slots
from workflows import RenderWorkflow

TASK_QUEUE = "render"


def make_worker(client: Client, **limits) -> Worker:
    return Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[RenderWorkflow],
        activities=[render],
        **limits,
    )


async def main():
    client = await Client.connect("localhost:7233")
    worker = make_worker(
        client,
        max_concurrent_activities=max_slots(),
        max_activities_per_second=max_per_second(),
    )
    await worker.run()


if __name__ == "__main__":
    asyncio.run(main())
