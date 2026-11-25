import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import create_greeting
from workflows import GreetSomeone

TASK_QUEUE = "hello-world"


def make_worker(client: Client) -> Worker:
    # TODO 1 start: create a Worker for TASK_QUEUE with the workflow and the activity
    raise NotImplementedError("TODO 1: create a Worker for TASK_QUEUE with the workflow and the activity")
    # TODO 1 end


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
