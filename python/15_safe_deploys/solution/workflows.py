from datetime import timedelta

from temporalio import workflow

with workflow.unsafe.imports_passed_through():
    from activities import bake, check_stock, take_order


@workflow.defn
class OrderWorkflow:
    @workflow.run
    async def run(self, name: str) -> str:
        timeout = timedelta(seconds=5)
        ticket = await workflow.execute_activity(take_order, name, start_to_close_timeout=timeout)
        # TODO 1 start [workflow]: run check_stock only when workflow.patched("check-stock") is true, so runs recorded by the old code still replay
        if workflow.patched("check-stock"):
            await workflow.execute_activity(check_stock, ticket, start_to_close_timeout=timeout)
        #? await workflow.execute_activity(check_stock, ticket, start_to_close_timeout=timeout)
        # TODO 1 end
        return await workflow.execute_activity(bake, ticket, start_to_close_timeout=timeout)
