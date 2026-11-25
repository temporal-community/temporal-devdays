from datetime import timedelta

from temporalio.client import (
    Client,
    Schedule,
    ScheduleActionStartWorkflow,
    ScheduleIntervalSpec,
    ScheduleSpec,
)

from workflows import ReportWorkflow

SCHEDULE_ID = "nightly-report"
TASK_QUEUE = "reports"


def build_schedule() -> Schedule:
    # TODO 1 start: return a Schedule that starts ReportWorkflow (id "report", task queue TASK_QUEUE) every minute
    return Schedule(
        action=ScheduleActionStartWorkflow(
            ReportWorkflow.run, id="report", task_queue=TASK_QUEUE),
        spec=ScheduleSpec(
            intervals=[ScheduleIntervalSpec(every=timedelta(minutes=1))]),
    )
    # TODO 1 end


async def create_schedule(client: Client) -> None:
    # TODO 2 start: create the Schedule with client.create_schedule, using SCHEDULE_ID and build_schedule()
    await client.create_schedule(SCHEDULE_ID, build_schedule())
    # TODO 2 end
