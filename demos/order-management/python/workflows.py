import asyncio
from datetime import timedelta

from temporalio import workflow
from temporalio.common import RetryPolicy
from temporalio.exceptions import ActivityError

with workflow.unsafe.imports_passed_through():
    from activities import charge, check_stock, release_stock, reserve_stock, ship_item
    from models import OrderInput, OrderOutput, OrderStatus

OPTS = dict(
    start_to_close_timeout=timedelta(seconds=5),
    retry_policy=RetryPolicy(initial_interval=timedelta(seconds=1), maximum_attempts=5),
)


@workflow.defn
class ShipmentWorkflow:
    @workflow.run
    async def run(self, order_id: str, address: str, items: list[str]) -> str:
        await asyncio.gather(*[
            workflow.execute_activity(ship_item, args=[order_id, item], **OPTS)
            for item in items
        ])
        return f"TRK-{order_id}"


@workflow.defn
class OrderWorkflow:
    def __init__(self) -> None:
        self.step = "starting"
        self.address = ""
        self.approved = False

    @workflow.run
    async def run(self, order: OrderInput) -> OrderOutput:
        self.address = order.address
        self.step = "checking stock"
        items = await workflow.execute_activity(check_stock, order, **OPTS)

        self.step = "reserving stock"
        undo = [release_stock]  # registered before the step, as in exercise 10
        await workflow.execute_activity(reserve_stock, order, **OPTS)

        if order.scenario == "approval":
            self.step = "waiting for approval"
            try:
                await workflow.wait_condition(lambda: self.approved, timeout=timedelta(hours=1))
            except asyncio.TimeoutError:
                return await self.cancel(order, undo, "approval timed out")

        self.step = "charging card"
        try:
            await workflow.execute_activity(charge, order, **OPTS)
        except ActivityError:
            return await self.cancel(order, undo, "card declined")

        self.step = "shipping"
        tracking_id = await workflow.execute_child_workflow(
            ShipmentWorkflow.run,
            args=[order.order_id, self.address, items],
            id=f"ship-{order.order_id}",
        )
        self.step = "done"
        return OrderOutput("shipped", tracking_id, self.address)

    async def cancel(self, order: OrderInput, undo: list, reason: str) -> OrderOutput:
        self.step = "cancelling"
        for step in reversed(undo):
            await workflow.execute_activity(step, order, **OPTS)
        self.step = "cancelled"
        return OrderOutput(f"cancelled: {reason}", "", self.address)

    @workflow.signal
    def approve(self) -> None:
        self.approved = True

    @workflow.update
    def change_address(self, address: str) -> str:
        self.address = address
        return address

    @change_address.validator
    def check_address(self, address: str) -> None:
        if len(address.strip()) < 5:
            raise ValueError("address is too short")

    @workflow.query
    def status(self) -> OrderStatus:
        return OrderStatus(self.step, self.address)
