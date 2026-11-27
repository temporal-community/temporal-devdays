import asyncio
import time

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import RenderWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    start = time.monotonic()
    result = await client.execute_workflow(
        RenderWorkflow.run, [f"item-{i}" for i in range(30)], id="render-30", task_queue=TASK_QUEUE)
    print(f"rendered {len(result)} items in {time.monotonic() - start:.1f} seconds")


if __name__ == "__main__":
    asyncio.run(main())
