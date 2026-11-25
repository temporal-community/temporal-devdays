import asyncio

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import GreetSomeone


async def main():
    client = await Client.connect("localhost:7233")
    result = await client.execute_workflow(
        GreetSomeone.run,
        "Temporal",
        id="my-first-workflow",
        task_queue=TASK_QUEUE,
    )
    print(result)


if __name__ == "__main__":
    asyncio.run(main())
