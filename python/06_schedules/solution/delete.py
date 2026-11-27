import asyncio

from temporalio.client import Client

from schedules import SCHEDULE_ID, delete_schedule


async def main():
    client = await Client.connect("localhost:7233")
    await delete_schedule(client)
    print(f"Deleted {SCHEDULE_ID}")


if __name__ == "__main__":
    asyncio.run(main())
