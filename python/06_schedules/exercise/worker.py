import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from schedules import TASK_QUEUE
from workflows import ReportWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    await Worker(client, task_queue=TASK_QUEUE, workflows=[ReportWorkflow]).run()


if __name__ == "__main__":
    asyncio.run(main())
