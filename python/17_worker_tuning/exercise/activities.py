import asyncio

from temporalio import activity

stats = {"running": 0, "max_running": 0}   # lets the check see how many ran at once


@activity.defn
async def render(item: str) -> str:
    stats["running"] += 1
    stats["max_running"] = max(stats["max_running"], stats["running"])
    await asyncio.sleep(0.2)
    stats["running"] -= 1
    return f"{item} rendered"
