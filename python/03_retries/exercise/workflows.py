from temporalio.exceptions import ApplicationError
from datetime import timedelta

from temporalio import workflow
from temporalio.common import RetryPolicy

with workflow.unsafe.imports_passed_through():
    from activities import charge


@workflow.defn
class PaymentWorkflow:
    @workflow.run
    async def run(self, card: str) -> str:
        # TODO 1 start [workflow]: call charge with a RetryPolicy: initial_interval of 10 ms and maximum_attempts of 5
        raise ApplicationError("TODO 1: call charge with a RetryPolicy: initial_interval of 10 ms and maximum_attempts of 5", non_retryable=True)
        # TODO 1 end
