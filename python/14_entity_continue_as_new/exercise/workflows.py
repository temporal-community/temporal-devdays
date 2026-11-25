from dataclasses import dataclass, field

from temporalio import workflow


@dataclass
class CartState:
    items: list[str] = field(default_factory=list)
    runs: int = 1


@workflow.defn
class CartWorkflow:
    def __init__(self) -> None:
        self.items: list[str] = []
        self.runs = 1
        self.added = 0
        self.closed = False

    @workflow.run
    async def run(self, state: CartState, max_per_run: int = 10) -> CartState:
        self.items = list(state.items)
        self.runs = state.runs
        await workflow.wait_condition(lambda: self.added >= max_per_run or self.closed)
        if self.closed:
            return CartState(self.items, self.runs)
        # TODO 1 start [workflow]: continue as new with the items so far and runs + 1, so the history stays small
        await workflow.wait_condition(lambda: self.closed)
        return CartState(self.items, self.runs)
        # TODO 1 end

    @workflow.signal
    def add_item(self, item: str) -> None:
        self.items.append(item)
        self.added += 1

    @workflow.signal
    def close(self) -> None:
        self.closed = True
