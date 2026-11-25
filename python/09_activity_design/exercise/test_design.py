import asyncio

import activities
from models import Claim
from temporalio.testing import WorkflowEnvironment
from worker import TASK_QUEUE, make_worker
from workflows import ClaimWorkflow, OnboardWorkflow


async def run(workflow_run, arg, workflow_id):
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            return await asyncio.wait_for(
                env.client.execute_workflow(
                    workflow_run, arg, id=workflow_id, task_queue=TASK_QUEUE),
                30,
            )


async def test_pure_logic_stays_in_the_workflow():
    activities.calls.clear()
    assert await run(ClaimWorkflow.run, Claim(20000, "dental"), "t1") == "review"
    assert await run(ClaimWorkflow.run, Claim(100, "dental"), "t2") == "approve"
    assert activities.calls == [], "TODO 1: the decision is plain logic, keep it in the Workflow, not in an Activity"


async def test_split_the_activity_that_calls_two_services():
    activities.users.clear()
    activities.emails.clear()
    assert await run(OnboardWorkflow.run, "alice", "t3") == "welcome"
    assert activities.users == ["alice"], (
        f"TODO 2: the user was added {len(activities.users)} times, "
        "split the Activity so a retry only repeats the email")
    assert activities.emails == ["alice"]
