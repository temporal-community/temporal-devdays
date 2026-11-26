import asyncio
import uuid

from temporalio.client import Client

import telemetry
from models import PizzaOrder
from worker import TASK_QUEUE
from workflows import PizzaWorkflow


async def main():
    tracing = telemetry.setup("pizza-starter")
    client = await Client.connect("localhost:7233", interceptors=[tracing])
    result = await client.execute_workflow(
        PizzaWorkflow.run, PizzaOrder(uuid.uuid4().hex[:6], "margherita"),
        id=f"pizza-{uuid.uuid4().hex[:6]}", task_queue=TASK_QUEUE)
    print(result)


if __name__ == "__main__":
    asyncio.run(main())
