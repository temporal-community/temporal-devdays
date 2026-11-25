import asyncio

from temporalio.client import Client

from notifications import run_now


async def main():
    client = await Client.connect("localhost:7233")
    print(await run_now(client))


if __name__ == "__main__":
    asyncio.run(main())
