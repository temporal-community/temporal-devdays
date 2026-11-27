from datetime import timedelta

from temporalio.client import (
    Client,
    Schedule,
    ScheduleActionStartWorkflow,
    ScheduleIntervalSpec,
    ScheduleSpec,
    ScheduleState,
)

from workflows import ReportWorkflow

SCHEDULE_ID = "nightly-report"
TASK_QUEUE = "reports"


def build_schedule() -> Schedule:
    # TODO 1 start: return a Schedule that starts ReportWorkflow (id "report", task queue TASK_QUEUE) every minute, limited to 5 runs (ScheduleState)
    raise NotImplementedError("TODO 1: return a Schedule that starts ReportWorkflow (id \"report\", task queue TASK_QUEUE) every minute, limited to 5 runs (ScheduleState)")
    # TODO 1 end


async def create_schedule(client: Client) -> None:
    # TODO 2 start: create the Schedule with client.create_schedule, using SCHEDULE_ID and build_schedule()
    raise NotImplementedError("TODO 2: create the Schedule with client.create_schedule, using SCHEDULE_ID and build_schedule()")
    # TODO 2 end


async def delete_schedule(client: Client) -> None:
    await client.get_schedule_handle(SCHEDULE_ID).delete()
