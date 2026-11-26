import asyncio
import sys

from temporalio.client import Client
from temporalio.worker import Worker

from activities import charge, check_stock, refund_charge, release_stock, reserve_stock, ship_item
from workflows import OrderWorkflow, ShipmentWorkflow

TASK_QUEUE = "orders"

SERVICES = {
    "orders": dict(workflows=[OrderWorkflow, ShipmentWorkflow]),
    "inventory": dict(activities=[check_stock, reserve_stock, release_stock]),
    "payments": dict(activities=[charge, refund_charge]),
    "shipping": dict(activities=[ship_item]),
}


def make_workers(client: Client, names: list[str] | None = None) -> list[Worker]:
    return [Worker(client, task_queue=n, **SERVICES[n]) for n in (names or SERVICES)]


async def main(names: list[str]):
    client = await Client.connect("localhost:7233")
    await asyncio.gather(*[w.run() for w in make_workers(client, names)])


if __name__ == "__main__":
    asyncio.run(main(sys.argv[1:]))
