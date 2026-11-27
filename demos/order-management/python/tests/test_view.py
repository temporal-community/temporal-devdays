import asyncio
from contextlib import AsyncExitStack

import app as web
import pytest
from temporalio.testing import WorkflowEnvironment
from worker import make_workers


@pytest.fixture
async def page():
    async with await WorkflowEnvironment.start_local() as env:
        web.client = env.client
        async with AsyncExitStack() as stack:
            async def run(*names):
                for w in make_workers(env.client, list(names) or None):
                    await stack.enter_async_context(w)
            yield web.app.test_client(), run


async def place(http, scenario: str) -> str:
    reply = await http.post("/start", json={"scenario": scenario, "address": "12 Main Street"})
    return (await reply.get_json())["order_id"]


async def view(http, order_id: str, until, seconds: int = 20) -> dict:
    for _ in range(seconds * 4):
        data = await (await http.get(f"/timeline/{order_id}")).get_json()
        if until(data):
            return data
        await asyncio.sleep(0.25)
    raise AssertionError(f"timeline never reached the expected state: {data}")


def names(data: dict, **match) -> list[str]:
    return [s["name"] for s in data["steps"] if all(s[k] == v for k, v in match.items())]


async def test_the_timeline_shows_the_steps_the_child_and_who_ran_them(page):
    http, run = page
    await run()
    data = await view(http, await place(http, "happy"), lambda d: d["state"] == "COMPLETED")
    assert names(data, kind="activity") == ["check_stock", "reserve_stock", "charge"]
    assert {s["language"] for s in data["steps"] if s["kind"] == "activity"} == {"python"}
    child = next(s for s in data["steps"] if s["kind"] == "child")
    assert child["status"] == "done" and sorted(i["item"] for i in child["items"]) == ["keypad", "table legs", "table top"]
    assert all(i["status"] == "done" and i["service"] == "shipping" for i in child["items"])
    services = await (await http.get("/services")).get_json()
    assert [(s["name"], s["up"], s["language"]) for s in services] == [
        ("orders", True, "python"), ("inventory", True, "python"), ("payments", True, "python"), ("shipping", True, "python")]


async def test_the_timeline_shows_the_undo_steps_in_reverse_order(page):
    http, run = page
    await run()
    data = await view(http, await place(http, "ship_fails"), lambda d: d["state"] == "COMPLETED")
    assert names(data, undo=True) == ["refund_charge", "release_stock"]
    child = next(s for s in data["steps"] if s["kind"] == "child")
    assert child["status"] == "failed" and any(i["status"] == "failed" for i in child["items"])


async def test_the_timeline_shows_retries_while_they_happen(page):
    http, run = page
    await run()
    order = await place(http, "flaky_card")
    data = await view(http, order, lambda d: "retrying" in [s["status"] for s in d["steps"]])
    charge = next(s for s in data["steps"] if s["name"] == "charge")
    assert charge["attempt"] >= 2 and "payment API unavailable" in charge["detail"]
    data = await view(http, order, lambda d: d["state"] == "COMPLETED")
    assert next(s for s in data["steps"] if s["name"] == "charge")["attempt"] == 3


async def test_a_task_nobody_picks_up_shows_as_waiting_for_a_worker(page):
    http, run = page
    await run("orders", "inventory", "shipping")
    data = await view(http, await place(http, "happy"), lambda d: "waiting" in [s["status"] for s in d["steps"]])
    charge = next(s for s in data["steps"] if s["status"] == "waiting")
    assert charge["name"] == "charge" and "payments" in charge["detail"]
    services = {s["name"]: s for s in await (await http.get("/services")).get_json()}
    assert services["payments"]["up"] is False and services["inventory"]["up"] is True


async def test_the_approval_countdown_and_the_message_steps(page):
    http, run = page
    await run()
    order = await place(http, "approval")
    data = await view(http, order, lambda d: d["countdown_ends"] is not None)
    await http.post(f"/address/{order}", json={"address": "99 Long Road, Pune"})
    await http.post(f"/approve/{order}")
    data = await view(http, order, lambda d: d["state"] == "COMPLETED")
    assert names(data, kind="update") == ["change_address"] and names(data, kind="signal") == ["approve"]
    assert data["countdown_ends"] is None


async def test_recent_orders_remember_their_scenario(page):
    http, run = page
    await run()
    order = await place(http, "bad_card")
    for _ in range(40):
        recent = await (await http.get("/orders")).get_json()
        if recent and recent[0]["id"] == order:
            break
        await asyncio.sleep(0.25)
    assert recent[0]["scenario"] == "bad_card" and recent[0]["id"] == order
