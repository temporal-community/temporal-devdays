import asyncio

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import KitchenWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    print(await client.execute_workflow(
        KitchenWorkflow.run, args=["o1", ["a", "b", "c"]], id="kitchen-o1", task_queue=TASK_QUEUE))


if __name__ == "__main__":
    asyncio.run(main())
