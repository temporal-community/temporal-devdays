import asyncio

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import ApprovalWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    handle = await client.start_workflow(
        ApprovalWorkflow.run, 500, id="approval-1", task_queue=TASK_QUEUE)
    await handle.signal(ApprovalWorkflow.approve, "alice")
    print(await handle.result())


if __name__ == "__main__":
    asyncio.run(main())
