import asyncio

from temporalio.testing import WorkflowEnvironment

from worker import TASK_QUEUE, make_worker
from workflows import AgentWorkflow


async def test_the_model_and_the_tool_run_as_activities():
    async with await WorkflowEnvironment.start_time_skipping() as env:
        async with make_worker(env.client):
            handle = await env.client.start_workflow(
                AgentWorkflow.run, "What is the weather in Pune?", id="t1", task_queue=TASK_QUEUE)
            answer = await asyncio.wait_for(handle.result(), 30)
            history = await handle.fetch_history()
    scheduled = [
        e.activity_task_scheduled_event_attributes
        for e in history.events if e.HasField("activity_task_scheduled_event_attributes")
    ]
    names = [s.activity_type.name for s in scheduled]
    assert answer == "It is sunny in Pune"
    assert names.count("llm_decide") == 2, "TODO 1: the model call must be an Activity, not plain code in the Workflow"
    assert names.count("run_tool") == 1, "TODO 2: the tool call must be an Activity, not plain code in the Workflow"
    policy = next(s for s in scheduled if s.activity_type.name == "llm_decide").retry_policy
    assert policy.maximum_attempts == 3, "TODO 1: retry the model call at most 3 times"
