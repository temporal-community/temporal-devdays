from datetime import timedelta

from temporalio import workflow
from temporalio.common import RetryPolicy

with workflow.unsafe.imports_passed_through():
    from activities import llm_decide, run_tool
    from llm import mock_llm
    from models import Message


@workflow.defn
class AgentWorkflow:
    @workflow.run
    async def run(self, question: str) -> str:
        timeout = timedelta(seconds=5)
        messages = [Message("user", question)]
        for _ in range(5):
            # TODO 1 start [workflow]: ask the model with the llm_decide Activity, retried at most 3 times
            decision = await workflow.execute_activity(
                llm_decide,
                messages,
                start_to_close_timeout=timeout,
                retry_policy=RetryPolicy(initial_interval=timedelta(milliseconds=10), maximum_attempts=3),
            )
            #? decision = mock_llm(messages)
            # TODO 1 end
            if decision.answer:
                return decision.answer
            # TODO 2 start [workflow]: run the tool with the run_tool Activity and add its result as a tool message
            result = await workflow.execute_activity(
                run_tool, decision, start_to_close_timeout=timeout)
            #? result = "sunny"
            # TODO 2 end
            messages.append(Message("tool", result))
        return "gave up"
