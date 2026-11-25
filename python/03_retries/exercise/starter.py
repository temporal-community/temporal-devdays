import asyncio
import sys

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import PaymentWorkflow


async def main():
    card = sys.argv[1] if len(sys.argv) > 1 else "flaky"
    client = await Client.connect("localhost:7233")
    result = await client.execute_workflow(
        PaymentWorkflow.run, card, id=f"payment-{card}", task_queue=TASK_QUEUE)
    print(result)


if __name__ == "__main__":
    asyncio.run(main())
