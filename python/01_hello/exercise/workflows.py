from temporalio.exceptions import ApplicationError
from datetime import timedelta

from temporalio import workflow

with workflow.unsafe.imports_passed_through():
    from activities import create_greeting


@workflow.defn
class GreetSomeone:
    @workflow.run
    async def run(self, name: str) -> str:
        # TODO 2 start [workflow]: call create_greeting with workflow.execute_activity and return its result
        raise ApplicationError("TODO 2: call create_greeting with workflow.execute_activity and return its result", non_retryable=True)
        # TODO 2 end
