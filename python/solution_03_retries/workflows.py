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
        return await workflow.execute_activity(
            charge,
            card,
            start_to_close_timeout=timedelta(seconds=5),
            retry_policy=RetryPolicy(
                initial_interval=timedelta(milliseconds=10),
                maximum_attempts=5,
            ),
        )
        # TODO 1 end
