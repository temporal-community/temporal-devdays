import asyncio

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import OrderWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    print(await client.execute_workflow(OrderWorkflow.run, "Ana", id="order-ana", task_queue=TASK_QUEUE))


if __name__ == "__main__":
    asyncio.run(main())
