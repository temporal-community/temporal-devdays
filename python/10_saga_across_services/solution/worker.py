import asyncio
import sys

from temporalio.client import Client
from temporalio.worker import Worker

from accounts_activities import deposit, undo_withdraw, withdraw
from fraud_activities import check_fraud
from notify_activities import send_receipt
from workflows import TransferWorkflow

SERVICES = {
    "orchestrator": dict(workflows=[TransferWorkflow]),
    "accounts": dict(activities=[withdraw, deposit, undo_withdraw]),
    "fraud": dict(activities=[check_fraud]),
    "notifications": dict(activities=[send_receipt]),
}


def make_workers(client: Client) -> list[Worker]:
    return [Worker(client, task_queue=name, **parts) for name, parts in SERVICES.items()]


async def main(service: str):
    client = await Client.connect("localhost:7233")
    await Worker(client, task_queue=service, **SERVICES[service]).run()


if __name__ == "__main__":
    asyncio.run(main(sys.argv[1]))
