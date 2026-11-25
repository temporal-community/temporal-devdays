import asyncio
from datetime import timedelta

from temporalio import workflow

with workflow.unsafe.imports_passed_through():
    from activities import get_distance, send_bill
    from models import Bill, PizzaOrder


@workflow.defn(name="PizzaWorkflow")
class SwappedPizzaWorkflow:
    """The same Workflow with the Timer moved before the distance check."""

    @workflow.run
    async def run(self, order: PizzaOrder) -> str:
        timeout = timedelta(seconds=5)
        await asyncio.sleep(order.prep_seconds)
        await workflow.execute_activity(
            get_distance, order.address, start_to_close_timeout=timeout)
        bill = Bill(order.customer.id, sum(p.price for p in order.items))
        return await workflow.execute_activity(
            send_bill, bill, start_to_close_timeout=timeout)
