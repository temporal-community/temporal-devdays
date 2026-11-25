from temporalio import activity
from temporalio.exceptions import ApplicationError

calls: list[str] = []  # lets the check count attempts


@activity.defn
async def charge(card: str) -> str:
    calls.append(card)
    if card == "bad":
        # TODO 2 start: raise ApplicationError("card declined", type="InvalidCard", non_retryable=True)
        raise ApplicationError("card declined", type="InvalidCard", non_retryable=True)
        # TODO 2 end
    attempt = activity.info().attempt
    if card == "flaky" and attempt < 3:
        raise Exception("temporary failure")
    return f"charged on attempt {attempt}"
