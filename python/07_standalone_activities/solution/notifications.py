from datetime import timedelta

from temporalio.client import Client

from activities import SendNotificationInput, send_notification

TASK_QUEUE = "notifications"
ACTIVITY_ID = "welcome-notification-user-123"
INPUT = SendNotificationInput("user-123", "Welcome!")


async def run_now(client: Client) -> str:
    # TODO 1 start: run send_notification as a Standalone Activity with client.execute_activity and return the result
    return await client.execute_activity(
        send_notification,
        INPUT,
        id=ACTIVITY_ID,
        task_queue=TASK_QUEUE,
        start_to_close_timeout=timedelta(seconds=30),
    )
    # TODO 1 end


async def run_later(client: Client, delay: timedelta = timedelta(hours=6), activity_id: str = ACTIVITY_ID):
    # TODO 2 start: start it with client.start_activity, start_delay=delay (6 hours by default), id=activity_id, and return the handle
    return await client.start_activity(
        send_notification,
        INPUT,
        id=activity_id,
        task_queue=TASK_QUEUE,
        start_to_close_timeout=timedelta(seconds=30),
        schedule_to_close_timeout=timedelta(days=7),
        start_delay=delay,
    )
    # TODO 2 end
