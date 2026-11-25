from datetime import timedelta

from temporalio import workflow
from temporalio.exceptions import ActivityError

with workflow.unsafe.imports_passed_through():
    from accounts_activities import deposit, undo_withdraw, withdraw
    from fraud_activities import check_fraud
    from models import TransferRequest
    from notify_activities import send_receipt


@workflow.defn
class TransferWorkflow:
    @workflow.run
    async def run(self, req: TransferRequest) -> str:
        opts = dict(start_to_close_timeout=timedelta(seconds=5))
        undo = []
        try:
            await workflow.execute_activity(
                check_fraud, req, task_queue="fraud", **opts)
            # TODO 1 start [workflow]: add undo_withdraw to the undo list BEFORE calling withdraw
            undo.append(undo_withdraw)
            await workflow.execute_activity(
                withdraw, req, task_queue="accounts", **opts)
            #? await workflow.execute_activity(
            #?     withdraw, req, task_queue="accounts", **opts)
            #? undo.append(undo_withdraw)
            # TODO 1 end
            await workflow.execute_activity(
                deposit, req, task_queue="accounts", **opts)
            await workflow.execute_activity(
                send_receipt, req, task_queue="notifications", **opts)
            return "Transfer completed"
        except ActivityError:
            # TODO 2 start [workflow]: run the undo steps in reverse order, each as an Activity on the accounts queue
            for step in reversed(undo):
                await workflow.execute_activity(
                    step, req, task_queue="accounts", **opts)
            #? pass
            # TODO 2 end
            return "Transfer failed, money returned"
