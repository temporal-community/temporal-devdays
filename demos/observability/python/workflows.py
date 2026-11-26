import asyncio
from datetime import timedelta

from temporalio import workflow
from temporalio.common import SearchAttributeKey

with workflow.unsafe.imports_passed_through():
    from activities import bake, deliver, take_order
    from models import PizzaOrder

STEP = SearchAttributeKey.for_keyword("Step")
OPTS = dict(start_to_close_timeout=timedelta(seconds=10))


@workflow.defn
class PizzaWorkflow:
    @workflow.run
    async def run(self, order: PizzaOrder) -> str:
        workflow.logger.info("order started: %s", order.pizza)

        workflow.upsert_search_attributes([STEP.value_set("taking order")])
        await workflow.execute_activity(take_order, order, **OPTS)

        workflow.upsert_search_attributes([STEP.value_set("preparing")])
        await asyncio.sleep(order.prep_seconds)

        workflow.upsert_search_attributes([STEP.value_set("baking")])
        await workflow.execute_activity(bake, order, **OPTS)

        workflow.upsert_search_attributes([STEP.value_set("delivering")])
        result = await workflow.execute_activity(deliver, order, **OPTS)

        workflow.upsert_search_attributes([STEP.value_set("done")])
        workflow.logger.info("order finished")
        return result
