import asyncio

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import TransferWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    handle = await client.start_workflow(
        TransferWorkflow.run, id="transfer-1", task_queue=TASK_QUEUE)
    print("Started", handle.id)


if __name__ == "__main__":
    asyncio.run(main())
