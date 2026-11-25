import asyncio

from temporalio.client import Client

from models import Customer, Pizza, PizzaOrder
from worker import TASK_QUEUE
from workflows import PizzaWorkflow


async def main():
    client = await Client.connect("localhost:7233")
    order = PizzaOrder(Customer("c1"), [Pizza("Margherita", 12)], "1 Main St", prep_seconds=2)
    result = await client.execute_workflow(
        PizzaWorkflow.run, order, id="order-1", task_queue=TASK_QUEUE)
    print(result)


if __name__ == "__main__":
    asyncio.run(main())
