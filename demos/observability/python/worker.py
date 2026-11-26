import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

import telemetry
from activities import bake, deliver, take_order
from workflows import PizzaWorkflow

TASK_QUEUE = "pizza"


async def main():
    tracing = telemetry.setup("pizza-worker", metrics_port=9464)
    client = await Client.connect("localhost:7233", interceptors=[tracing])
    await Worker(client, task_queue=TASK_QUEUE, workflows=[PizzaWorkflow],
                 activities=[take_order, bake, deliver]).run()


if __name__ == "__main__":
    asyncio.run(main())
