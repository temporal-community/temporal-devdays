from temporalio.client import Client
from temporalio.worker import Worker

from activities import get_distance, send_bill
from workflows import PizzaWorkflow

TASK_QUEUE = "pizza"


def make_worker(client: Client, activities=(get_distance, send_bill)) -> Worker:
    return Worker(
        client,
        task_queue=TASK_QUEUE,
        workflows=[PizzaWorkflow],
        activities=list(activities),
    )
