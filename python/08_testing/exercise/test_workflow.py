import time

import pytest
from temporalio import activity
from temporalio.client import WorkflowFailureError
from temporalio.testing import WorkflowEnvironment

from activities import send_bill
from models import Customer, Pizza, PizzaOrder
from worker import TASK_QUEUE, make_worker
from workflows import PizzaWorkflow

ORDER = PizzaOrder(Customer("c1"), [Pizza("Margherita", 12)], "1 Main St")


@activity.defn(name="get_distance")
async def far_away(address: str) -> int:
    return 40


async def run_pizza(activities=None):
    async with await WorkflowEnvironment.start_time_skipping() as env:
        worker = make_worker(env.client, activities) if activities else make_worker(env.client)
        async with worker:
            return await env.client.execute_workflow(
                PizzaWorkflow.run, ORDER, id="t1", task_queue=TASK_QUEUE)


async def test_thirty_minute_timer_finishes_instantly():
    # TODO 2 start: run the order with run_pizza(), check the bill starts with TPD and that it took under 20 seconds
    raise NotImplementedError("TODO 2: run the order with run_pizza(), check the bill starts with TPD and that it took under 20 seconds")
    # TODO 2 end


async def test_too_far_fails_the_workflow():
    # TODO 3 start: run the order with run_pizza([far_away, send_bill]) and expect a WorkflowFailureError
    raise NotImplementedError("TODO 3: run the order with run_pizza([far_away, send_bill]) and expect a WorkflowFailureError")
    # TODO 3 end
