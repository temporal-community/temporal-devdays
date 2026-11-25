from temporalio import activity
from temporalio.exceptions import ApplicationError

from models import TransferRequest

withdrawn: list[str] = []   # lets the check see what happened
undone: list[str] = []


@activity.defn
async def withdraw(req: TransferRequest) -> None:
    withdrawn.append(req.from_account)
    if req.fail_at == "withdraw":
        raise ApplicationError("withdraw failed after it landed", non_retryable=True)


@activity.defn
async def deposit(req: TransferRequest) -> None:
    if req.fail_at == "deposit":
        raise ApplicationError("deposit failed", non_retryable=True)


@activity.defn
async def undo_withdraw(req: TransferRequest) -> None:
    undone.append(req.from_account)
