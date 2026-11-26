from temporalio import activity
from temporalio.exceptions import ApplicationError

from models import OrderInput

calls: list[str] = []  # lets the tests see what ran


@activity.defn
async def check_stock(order: OrderInput) -> list[str]:
    calls.append("check_stock")
    return ["table top", "table legs", "keypad"]


@activity.defn
async def reserve_stock(order: OrderInput) -> None:
    calls.append("reserve_stock")


@activity.defn
async def release_stock(order: OrderInput) -> None:
    calls.append("release_stock")


@activity.defn
async def charge(order: OrderInput) -> None:
    calls.append("charge")
    if order.scenario == "bad_card":
        raise ApplicationError("card declined", non_retryable=True)
    if order.scenario == "flaky_card" and activity.info().attempt < 3:
        raise RuntimeError("payment API unavailable")


@activity.defn
async def ship_item(order_id: str, item: str) -> None:
    calls.append(f"ship:{item}")
