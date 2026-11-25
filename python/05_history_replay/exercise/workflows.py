import asyncio
from datetime import timedelta

from temporalio import workflow
from temporalio.exceptions import ApplicationError

with workflow.unsafe.imports_passed_through():
    from activities import get_distance, send_bill
    from models import Bill, PizzaOrder


@workflow.defn
class PizzaWorkflow:
    @workflow.run
    async def run(self, order: PizzaOrder) -> str:
        timeout = timedelta(seconds=5)
        total = sum(p.price for p in order.items)
        distance = await workflow.execute_activity(
            get_distance, order.address, start_to_close_timeout=timeout)
        if order.is_delivery and distance > 25:
            raise ApplicationError("too far")
        await asyncio.sleep(order.prep_seconds)
        bill = Bill(order.customer.id, total)
        return await workflow.execute_activity(
            send_bill, bill, start_to_close_timeout=timeout)
