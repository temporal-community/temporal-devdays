import asyncio

from temporalio.client import Client

from schedules import SCHEDULE_ID, create_schedule


async def main():
    client = await Client.connect("localhost:7233")
    await create_schedule(client)
    print(f"Created {SCHEDULE_ID}")
    print(f"It stops after 5 runs. To stop sooner: uv run python delete.py")


if __name__ == "__main__":
    asyncio.run(main())
