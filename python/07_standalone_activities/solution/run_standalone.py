import asyncio
from datetime import timedelta

from temporalio.client import Client

from notifications import ACTIVITY_ID, run_later, run_now


async def main():
    client = await Client.connect("localhost:7233")
    print(await run_now(client))
    handle = await run_later(client, timedelta(seconds=10), ACTIVITY_ID + "-later")
    print("scheduled, runs in 10 seconds")
    print(await handle.result())


if __name__ == "__main__":
    asyncio.run(main())
