import asyncio

from temporalio import activity

from models import PizzaOrder


@activity.defn
async def take_order(order: PizzaOrder) -> None:
    activity.logger.info("taking order for %s", order.pizza)
    await asyncio.sleep(0.2)


@activity.defn
async def bake(order: PizzaOrder) -> None:
    activity.logger.info("baking %s, attempt %s", order.pizza, activity.info().attempt)
    await asyncio.sleep(0.5)


@activity.defn
async def deliver(order: PizzaOrder) -> str:
    activity.logger.info("delivering %s", order.pizza)
    await asyncio.sleep(0.3)
    return f"{order.pizza} delivered"
