from temporalio import workflow


@workflow.defn
class TransferWorkflow:
    def __init__(self) -> None:
        self.approved = False
        self.amount = 100
        self.status = "waiting"

    @workflow.run
    async def run(self) -> str:
        await workflow.wait_condition(lambda: self.approved)
        self.status = "sent"
        return f"sent {self.amount}"

    @workflow.signal
    def approve(self) -> None:
        # TODO 1 start [workflow]: set self.approved to True
        self.approved = True
        # TODO 1 end

    @workflow.query
    def get_status(self) -> str:
        # TODO 2 start: return self.status
        return self.status
        # TODO 2 end

    @workflow.update
    def set_amount(self, amount: int) -> int:
        # TODO 3 start [workflow]: store amount in self.amount and return it
        self.amount = amount
        return self.amount
        # TODO 3 end

    @set_amount.validator
    def check_amount(self, amount: int) -> None:
        # TODO 4 start [workflow]: raise ValueError when amount is not positive
        if amount <= 0:
            raise ValueError("amount must be positive")
        # TODO 4 end
