import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import llm_decide, run_tool
from workflows import AgentWorkflow

TASK_QUEUE = "agent"


def make_worker(client: Client) -> Worker:
    return Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[AgentWorkflow],
        activities=[llm_decide, run_tool],
    )


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
