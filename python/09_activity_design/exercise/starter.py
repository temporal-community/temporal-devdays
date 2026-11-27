import asyncio

from temporalio.client import Client

from models import Claim
from worker import TASK_QUEUE
from workflows import ClaimWorkflow, OnboardWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    decision = await client.execute_workflow(
        ClaimWorkflow.run, Claim(20000, "surgery"), id="claim-1", task_queue=TASK_QUEUE)
    print(f"claim decision: {decision}")
    print(await client.execute_workflow(OnboardWorkflow.run, "Ana", id="onboard-ana", task_queue=TASK_QUEUE))


if __name__ == "__main__":
    asyncio.run(main())
