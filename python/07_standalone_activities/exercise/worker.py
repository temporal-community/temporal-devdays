import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import send_notification
from notifications import TASK_QUEUE


async def main():
    client = await Client.connect("localhost:7233")
    await Worker(client, task_queue=TASK_QUEUE, activities=[send_notification]).run()


if __name__ == "__main__":
    asyncio.run(main())
