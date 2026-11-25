from temporalio import activity


@activity.defn
async def take_order(name: str) -> str:
    return f"ticket-{name}"


@activity.defn
async def bake(ticket: str) -> str:
    return f"baked {ticket}"
