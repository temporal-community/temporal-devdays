import asyncio
from datetime import timedelta

from temporalio import workflow

with workflow.unsafe.imports_passed_through():
    from activities import make_item


@workflow.defn
class ShipmentWorkflow:
    @workflow.run
    async def run(self, order_id: str) -> str:
        return f"shipped {order_id}"


@workflow.defn
class KitchenWorkflow:
    @workflow.run
    async def run(self, order_id: str, items: list[str]) -> list[str]:
        timeout = timedelta(seconds=10)
        # TODO 1 start [workflow]: start make_item for every item at once, then wait for all of them with asyncio.gather
        ready = []
        for item in items:
            ready.append(await workflow.execute_activity(make_item, item, start_to_close_timeout=timeout))
        # TODO 1 end
        # TODO 2 start [workflow]: ship the order in a child workflow with the id f"ship-{order_id}"
        shipped = f"shipped {order_id}"
        # TODO 2 end
        return [*ready, shipped]
