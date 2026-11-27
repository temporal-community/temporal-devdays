from datetime import timedelta

from activities import SendNotificationInput, send_notification
from notifications import run_later, run_now


class FakeClient:
    def __init__(self):
        self.calls = []

    async def execute_activity(self, *args, **kwargs):
        self.calls.append(("execute", args, kwargs))
        return "sent"

    async def start_activity(self, *args, **kwargs):
        self.calls.append(("start", args, kwargs))
        return "handle"


async def test_run_now_executes_the_activity():
    client = FakeClient()
    assert await run_now(client) == "sent"
    kind, args, kwargs = client.calls[0]
    assert kind == "execute"
    assert args[0] is send_notification
    assert args[1] == SendNotificationInput("user-123", "Welcome!")
    assert kwargs["id"] == "welcome-notification-user-123"
    assert kwargs["task_queue"] == "notifications"
    assert kwargs["start_to_close_timeout"] == timedelta(seconds=30)


async def test_run_later_starts_it_with_a_delay():
    client = FakeClient()
    assert await run_later(client) == "handle"
    kind, _, kwargs = client.calls[0]
    assert kind == "start"
    assert kwargs["start_delay"] == timedelta(hours=6)
    assert kwargs["schedule_to_close_timeout"] == timedelta(days=7)


async def test_run_later_uses_the_delay_and_id_it_is_given():
    client = FakeClient()
    await run_later(client, timedelta(seconds=10), "other-id")
    _, _, kwargs = client.calls[0]
    assert kwargs["start_delay"] == timedelta(seconds=10)
    assert kwargs["id"] == "other-id"
