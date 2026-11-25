from temporalio import activity

from models import TransferRequest


@activity.defn
async def send_receipt(req: TransferRequest) -> None:
    activity.logger.info(f"Receipt sent for {req.amount}")
