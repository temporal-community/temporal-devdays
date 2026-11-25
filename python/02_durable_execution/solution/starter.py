import asyncio

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import OrderWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    handle = await client.start_workflow(
        OrderWorkflow.run, "Margherita", id="order-1", task_queue=TASK_QUEUE)
    print("Started", handle.id)


if __name__ == "__main__":
    asyncio.run(main())
