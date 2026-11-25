import asyncio

import pytest
from temporalio.api.enums.v1 import EventType
from temporalio.client import WorkflowFailureError
from temporalio.testing import WorkflowEnvironment

import activities
from worker import TASK_QUEUE, make_worker
from workflows import PaymentWorkflow


async def run(card: str, wid: str):
    activities.calls.clear()
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                PaymentWorkflow.run, card, id=wid, task_queue=TASK_QUEUE)
            try:
                result = await asyncio.wait_for(handle.result(), 30)
            except WorkflowFailureError as err:
                return None, err, await handle.fetch_history()
            return result, None, await handle.fetch_history()


def root(err: Exception) -> Exception:
    while err.__cause__ is not None:
        err = err.__cause__
    return err


async def test_flaky_card_succeeds_on_third_attempt_with_a_retry_policy():
    result, err, history = await run("flaky", "t1")
    assert err is None, f"the workflow failed: {root(err)}"
    assert result == "charged on attempt 3"
    scheduled = next(
        e for e in history.events
        if e.event_type == EventType.EVENT_TYPE_ACTIVITY_TASK_SCHEDULED)
    policy = scheduled.activity_task_scheduled_event_attributes.retry_policy
    assert policy.maximum_attempts == 5, "TODO 1: set maximum_attempts=5 in the RetryPolicy"


async def test_bad_card_is_not_retried():
    _, err, _ = await run("bad", "t2")
    assert err is not None
    assert len(activities.calls) == 1, f"charge was retried {len(activities.calls)} times: {root(err)}"
