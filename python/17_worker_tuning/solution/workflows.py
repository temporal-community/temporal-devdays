import asyncio
from datetime import timedelta

from temporalio import workflow

with workflow.unsafe.imports_passed_through():
    from activities import render


@workflow.defn
class RenderWorkflow:
    @workflow.run
    async def run(self, items: list[str]) -> list[str]:
        return list(await asyncio.gather(*[
            workflow.execute_activity(render, item, start_to_close_timeout=timedelta(seconds=60))
            for item in items
        ]))
