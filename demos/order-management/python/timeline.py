import time

from temporalio.api.enums.v1 import PendingActivityState, TaskQueueType
from temporalio.api.taskqueue.v1 import TaskQueue
from temporalio.api.workflowservice.v1 import DescribeTaskQueueRequest
from temporalio.client import Client

QUEUES = ["orders", "inventory", "payments", "shipping"]
UNDO = {"release_stock", "refund_charge"}
FRESH_SECONDS = 60   # an idle Worker asks for work again about every 50 seconds
WAIT_SECONDS = 3     # a task nobody picks up for this long means no Worker is listening


def language(identities: list[str]) -> str:
    return next((i.split("-")[0] for i in identities if i.startswith(("python-", "java-"))), "")


async def services(client: Client) -> list[dict]:
    out = []
    for queue in QUEUES:
        kind = TaskQueueType.TASK_QUEUE_TYPE_WORKFLOW if queue == "orders" else TaskQueueType.TASK_QUEUE_TYPE_ACTIVITY
        reply = await client.workflow_service.describe_task_queue(DescribeTaskQueueRequest(
            namespace=client.namespace, task_queue=TaskQueue(name=queue), task_queue_type=kind))
        fresh = [p.identity for p in reply.pollers if time.time() - p.last_access_time.seconds < FRESH_SECONDS]
        out.append({"name": queue, "up": bool(fresh), "pollers": len(fresh), "language": language(fresh)})
    return out


def read(events) -> tuple[list[dict], float | None]:
    steps, by_event, timer = [], {}, None
    for e in events:
        if e.HasField("activity_task_scheduled_event_attributes"):
            a = e.activity_task_scheduled_event_attributes
            step = dict(kind="activity", name=a.activity_type.name, service=a.task_queue.name, status="running",
                        attempt=1, detail="", item="", undo=a.activity_type.name in UNDO, worker="", activity_id=a.activity_id)
            if len(a.input.payloads) > 1:   # ship_item(order_id, item, scenario): the item is the second argument
                step["item"] = a.input.payloads[1].data.decode().strip('"')
            by_event[e.event_id] = step
            steps.append(step)
        elif e.HasField("activity_task_started_event_attributes"):
            a = e.activity_task_started_event_attributes
            by_event[a.scheduled_event_id].update(attempt=a.attempt, worker=a.identity)
        elif e.HasField("activity_task_completed_event_attributes"):
            by_event[e.activity_task_completed_event_attributes.scheduled_event_id]["status"] = "done"
        elif e.HasField("activity_task_failed_event_attributes"):
            a = e.activity_task_failed_event_attributes
            by_event[a.scheduled_event_id].update(status="failed", detail=a.failure.message)
        elif e.HasField("activity_task_timed_out_event_attributes"):
            by_event[e.activity_task_timed_out_event_attributes.scheduled_event_id].update(status="failed", detail="timed out")
        elif e.HasField("child_workflow_execution_started_event_attributes"):
            a = e.child_workflow_execution_started_event_attributes
            by_event["child"] = dict(kind="child", name=a.workflow_type.name, service="orders", status="running",
                                     attempt=1, detail=a.workflow_execution.workflow_id, item="", undo=False, worker="")
            steps.append(by_event["child"])
        elif e.HasField("child_workflow_execution_completed_event_attributes"):
            by_event["child"]["status"] = "done"
        elif e.HasField("child_workflow_execution_failed_event_attributes"):
            by_event["child"].update(status="failed", detail="a child step failed for good")
        elif e.HasField("workflow_execution_signaled_event_attributes"):
            steps.append(dict(kind="signal", name=e.workflow_execution_signaled_event_attributes.signal_name,
                              service="orders", status="done", attempt=1, detail="", item="", undo=False, worker=""))
        elif e.HasField("workflow_execution_update_accepted_event_attributes"):
            a = e.workflow_execution_update_accepted_event_attributes
            steps.append(dict(kind="update", name=a.accepted_request.input.name,
                              service="orders", status="done", attempt=1, detail="", item="", undo=False, worker=""))
        elif e.HasField("timer_started_event_attributes"):
            timer = e.event_time.seconds + e.timer_started_event_attributes.start_to_fire_timeout.seconds
        elif e.HasField("timer_fired_event_attributes") or e.HasField("timer_canceled_event_attributes"):
            timer = None
    return steps, timer


async def steps_of(client: Client, workflow_id: str):
    handle = client.get_workflow_handle(workflow_id)
    description = await handle.describe()
    steps, timer = read((await handle.fetch_history()).events)
    for p in description.raw_description.pending_activities:
        step = next((s for s in steps if s.get("activity_id") == p.activity_id and s["status"] == "running"), None)
        if step is None:   # it finished between the two reads
            continue
        if p.last_failure.message:
            step.update(status="retrying", attempt=p.attempt, detail=p.last_failure.message)
        elif p.state == PendingActivityState.PENDING_ACTIVITY_STATE_SCHEDULED \
                and time.time() - p.scheduled_time.seconds > WAIT_SECONDS:
            step.update(status="waiting", detail=f"no Worker is picking this up on {step['service']}")
    for step in steps:
        step.pop("activity_id", None)
        step["language"] = language([step["worker"]])
    return description.status.name, steps, timer


async def timeline(client: Client, order_id: str) -> dict:
    state, steps, timer = await steps_of(client, f"order-{order_id}")
    for step in steps:
        if step["kind"] == "child":
            _, step["items"], _ = await steps_of(client, f"ship-{order_id}")
    return {"state": state, "steps": steps, "countdown_ends": timer}


async def orders(client: Client) -> list[dict]:
    out = []
    async for wf in client.list_workflows("WorkflowType='OrderWorkflow'", limit=20):
        memo = await wf.memo()
        out.append({"id": wf.id.removeprefix("order-"), "scenario": memo.get("scenario", ""),
                    "state": wf.status.name, "started": wf.start_time.isoformat()})
    return sorted(out, key=lambda o: o["started"], reverse=True)[:10]
