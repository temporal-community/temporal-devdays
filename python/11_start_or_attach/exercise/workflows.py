from temporalio import workflow


@workflow.defn
class TransferWorkflow:
    def __init__(self) -> None:
        self.approved = False

    @workflow.run
    async def run(self) -> str:
        await workflow.wait_condition(lambda: self.approved)
        return "approved"

    @workflow.signal
    def approve(self) -> None:
        self.approved = True


@workflow.defn
class OrderWorkflow:
    def __init__(self) -> None:
        self.items: list[str] = []
        self.closed = False

    @workflow.run
    async def run(self) -> list[str]:
        await workflow.wait_condition(lambda: self.closed)
        return self.items

    @workflow.signal
    def add_item(self, item: str) -> None:
        self.items.append(item)

    @workflow.signal
    def close(self) -> None:
        self.closed = True
