from datetime import timedelta

from temporalio import workflow

with workflow.unsafe.imports_passed_through():
    from activities import bake, take_order


@workflow.defn(name="OrderWorkflow")
class OrderWorkflowV1:
    """The first version of the Workflow, the one that was running before the change."""

    @workflow.run
    async def run(self, name: str) -> str:
        timeout = timedelta(seconds=5)
        ticket = await workflow.execute_activity(take_order, name, start_to_close_timeout=timeout)
        return await workflow.execute_activity(bake, ticket, start_to_close_timeout=timeout)
