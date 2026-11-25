from temporalio.exceptions import ApplicationError
import asyncio
from datetime import timedelta

from temporalio import workflow

with workflow.unsafe.imports_passed_through():
    from activities import bake, take_order


@workflow.defn
class OrderWorkflow:
    @workflow.run
    async def run(self, name: str, wait_seconds: int = 1800) -> str:
        timeout = timedelta(seconds=5)
        ticket = await workflow.execute_activity(
            take_order, name, start_to_close_timeout=timeout)
        # TODO 1 start [workflow]: wait wait_seconds with asyncio.sleep, this is a durable Timer
        raise ApplicationError("TODO 1: wait wait_seconds with asyncio.sleep, this is a durable Timer", non_retryable=True)
        # TODO 1 end
        return await workflow.execute_activity(
            bake, ticket, start_to_close_timeout=timeout)
