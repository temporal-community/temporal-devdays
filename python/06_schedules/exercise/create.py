import asyncio

from temporalio.client import Client

from schedules import SCHEDULE_ID, create_schedule


async def main():
    client = await Client.connect("localhost:7233")
    await create_schedule(client)
    print(f"Created {SCHEDULE_ID}")


if __name__ == "__main__":
    asyncio.run(main())
