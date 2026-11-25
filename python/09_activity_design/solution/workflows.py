from datetime import timedelta

from temporalio import workflow
from temporalio.common import RetryPolicy

with workflow.unsafe.imports_passed_through():
    from activities import add_user, create_user, evaluate_claim, send_welcome
    from models import Claim


@workflow.defn
class ClaimWorkflow:
    @workflow.run
    async def run(self, claim: Claim) -> str:
        timeout = timedelta(seconds=5)
        # TODO 1 start [workflow]: decide with plain code in the Workflow, no Activity needed
        decision = "review" if claim.amount > 10000 or claim.kind == "surgery" else "approve"
        #? decision = await workflow.execute_activity(evaluate_claim, claim, start_to_close_timeout=timeout)
        # TODO 1 end
        return decision


@workflow.defn
class OnboardWorkflow:
    @workflow.run
    async def run(self, name: str) -> str:
        opts = dict(
            start_to_close_timeout=timedelta(seconds=5),
            retry_policy=RetryPolicy(initial_interval=timedelta(milliseconds=10)),
        )
        # TODO 2 start [workflow]: call add_user, then send_welcome, as two separate Activities
        await workflow.execute_activity(add_user, name, **opts)
        await workflow.execute_activity(send_welcome, name, **opts)
        #? await workflow.execute_activity(create_user, name, **opts)
        # TODO 2 end
        return "welcome"
