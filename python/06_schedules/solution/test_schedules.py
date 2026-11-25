from datetime import timedelta

from schedules import build_schedule, create_schedule


def test_builds_the_schedule():
    schedule = build_schedule()
    assert schedule.action.workflow == "ReportWorkflow"
    assert schedule.action.id == "report"
    assert schedule.action.task_queue == "reports"
    assert schedule.spec.intervals[0].every == timedelta(minutes=1)


async def test_creates_it_under_the_right_id():
    calls = []

    class FakeClient:
        async def create_schedule(self, schedule_id, schedule):
            calls.append((schedule_id, schedule))

    await create_schedule(FakeClient())
    assert [c[0] for c in calls] == ["nightly-report"]
