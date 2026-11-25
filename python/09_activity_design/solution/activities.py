from temporalio import activity

from models import Claim

calls: list[str] = []   # lets the check see which Activities ran
users: list[str] = []
emails: list[str] = []


@activity.defn
async def evaluate_claim(claim: Claim) -> str:
    calls.append("evaluate_claim")
    return "review" if claim.amount > 10000 or claim.kind == "surgery" else "approve"


@activity.defn
async def add_user(name: str) -> None:
    users.append(name)


@activity.defn
async def send_welcome(name: str) -> None:
    if activity.info().attempt == 1:
        raise Exception("email service is down")
    emails.append(name)


@activity.defn
async def create_user(name: str) -> None:
    users.append(name)
    if activity.info().attempt == 1:
        raise Exception("email service is down")
    emails.append(name)
