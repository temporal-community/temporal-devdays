import asyncio
import sys

from temporalio.client import Client

from models import TransferRequest
from workflows import TransferWorkflow


async def main():
    fail_at = sys.argv[1] if len(sys.argv) > 1 else ""
    client = await Client.connect("localhost:7233")
    result = await client.execute_workflow(
        TransferWorkflow.run,
        TransferRequest("alice", "bob", 100, fail_at),
        id=f"transfer-{fail_at or 'ok'}",
        task_queue="orchestrator",
    )
    print(result)


if __name__ == "__main__":
    asyncio.run(main())
