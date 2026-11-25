from temporalio import activity
from temporalio.exceptions import ApplicationError

from models import TransferRequest


@activity.defn
async def check_fraud(req: TransferRequest) -> None:
    if req.fail_at == "fraud":
        raise ApplicationError("flagged as fraud", non_retryable=True)
