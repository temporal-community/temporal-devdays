from temporalio import workflow


@workflow.defn
class ReportWorkflow:
    @workflow.run
    async def run(self) -> str:
        return "report ready"
