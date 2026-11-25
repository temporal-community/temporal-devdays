from temporalio import activity

from models import Bill


@activity.defn
async def get_distance(address: str) -> int:
    return 10


@activity.defn
async def send_bill(bill: Bill) -> str:
    return f"TPD-{bill.customer_id}-{bill.amount}"
