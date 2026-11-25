import asyncio
from datetime import timedelta

from temporalio import workflow


@workflow.defn
class ApprovalWorkflow:
    def __init__(self) -> None:
        self.approver: str | None = None

    @workflow.run
    async def run(self, amount: int) -> str:
        # TODO 1 start [workflow]: wait until self.approver is set with wait_condition and a 24 hour timeout, return "timed out" on timeout
        try:
            await workflow.wait_condition(
                lambda: self.approver is not None, timeout=timedelta(hours=24))
        except asyncio.TimeoutError:
            return "timed out"
        # TODO 1 end
        return f"approved by {self.approver}"

    @workflow.signal
    def approve(self, approver: str) -> None:
        # TODO 2 start [workflow]: remember who approved
        self.approver = approver
        # TODO 2 end
