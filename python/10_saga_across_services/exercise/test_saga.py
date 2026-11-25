import asyncio
from contextlib import AsyncExitStack

import accounts_activities
from models import TransferRequest
from temporalio.testing import WorkflowEnvironment
from worker import make_workers
from workflows import TransferWorkflow


async def run(fail_at: str) -> str:
    accounts_activities.withdrawn.clear()
    accounts_activities.undone.clear()
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with AsyncExitStack() as stack:
            for worker in make_workers(env.client):
                await stack.enter_async_context(worker)
            return await asyncio.wait_for(
                env.client.execute_workflow(
                    TransferWorkflow.run,
                    TransferRequest("alice", "bob", 100, fail_at),
                    id="t1",
                    task_queue="orchestrator",
                ),
                30,
            )


async def test_happy_path_has_nothing_to_undo():
    assert await run("") == "Transfer completed"
    assert accounts_activities.undone == []


async def test_deposit_failure_undoes_the_withdrawal():
    assert await run("deposit") == "Transfer failed, money returned"
    assert accounts_activities.undone == ["alice"], "TODO 2: run the undo steps when a step fails"


async def test_fraud_failure_has_nothing_to_undo():
    assert await run("fraud") == "Transfer failed, money returned"
    assert accounts_activities.undone == []


async def test_a_withdrawal_that_lands_and_then_fails_is_still_undone():
    assert await run("withdraw") == "Transfer failed, money returned"
    assert accounts_activities.undone == ["alice"], "TODO 1: register the undo BEFORE the step that can fail"
