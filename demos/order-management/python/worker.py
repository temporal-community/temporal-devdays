import asyncio

from temporalio.client import Client
from temporalio.worker import Worker

from activities import charge, check_stock, release_stock, reserve_stock, ship_item
from workflows import OrderWorkflow, ShipmentWorkflow

TASK_QUEUE = "orders"


def make_worker(client: Client) -> Worker:
    return Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[OrderWorkflow, ShipmentWorkflow],
        activities=[check_stock, reserve_stock, release_stock, charge, ship_item],
    )


async def main():
    client = await Client.connect("localhost:7233")
    await make_worker(client).run()


if __name__ == "__main__":
    asyncio.run(main())
