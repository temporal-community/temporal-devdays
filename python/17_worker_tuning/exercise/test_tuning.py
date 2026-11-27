import asyncio
import time

import activities
from temporalio.testing import WorkflowEnvironment
from tuning import max_per_second, max_slots
from worker import TASK_QUEUE, make_worker
from workflows import RenderWorkflow

ITEMS = [f"item-{i}" for i in range(12)]


async def run(**limits) -> float:
    activities.stats.update(running=0, max_running=0)
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client, **limits):
            start = time.monotonic()
            await asyncio.wait_for(env.client.execute_workflow(
                RenderWorkflow.run, ITEMS, id="t1", task_queue=TASK_QUEUE), 60)
            return time.monotonic() - start


async def test_activity_slots_cap_how_many_run_at_once():
    await run(max_concurrent_activities=max_slots())
    assert activities.stats["max_running"] == 3, "TODO 1: return 3, the most Activities this Worker may run at once"


async def test_the_rate_limit_spreads_the_starts_over_time():
    elapsed = await run(max_activities_per_second=max_per_second())
    assert elapsed > 1.8, "TODO 2: return 5, the most Activities this Worker may start per second"
