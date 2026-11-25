from dataclasses import dataclass

from temporalio import activity


@dataclass
class SendNotificationInput:
    user_id: str
    message: str


@activity.defn
async def send_notification(input: SendNotificationInput) -> str:
    return f"sent '{input.message}' to {input.user_id}"
