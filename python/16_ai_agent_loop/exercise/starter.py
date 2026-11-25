import asyncio

from temporalio.client import Client

from worker import TASK_QUEUE
from workflows import AgentWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    answer = await client.execute_workflow(
        AgentWorkflow.run, "What is the weather in Pune?", id="agent-1", task_queue=TASK_QUEUE)
    print(answer)


if __name__ == "__main__":
    asyncio.run(main())
