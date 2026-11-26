import uuid

from quart import Quart, jsonify, render_template, request
from temporalio.client import Client, WorkflowUpdateFailedError

from models import OrderInput
from worker import TASK_QUEUE
from workflows import OrderWorkflow

app = Quart(__name__)
client: Client


@app.before_serving
async def connect():
    global client
    client = await Client.connect("localhost:7233")


@app.get("/")
async def index():
    return await render_template("index.html")


@app.post("/start")
async def start():
    body = await request.get_json()
    order_id = uuid.uuid4().hex[:6]
    await client.start_workflow(
        OrderWorkflow.run,
        OrderInput(order_id, body["address"], body["scenario"]),
        id=f"order-{order_id}",
        task_queue=TASK_QUEUE,
    )
    return jsonify(order_id=order_id)


@app.get("/status/<order_id>")
async def status(order_id: str):
    handle = client.get_workflow_handle_for(OrderWorkflow.run, f"order-{order_id}")
    state = await handle.query(OrderWorkflow.status)
    desc = await handle.describe()
    out = {"step": state.step, "address": state.address, "state": desc.status.name}
    if desc.status.name == "COMPLETED":
        out["result"] = (await handle.result()).status
    return jsonify(out)


@app.post("/approve/<order_id>")
async def approve(order_id: str):
    await client.get_workflow_handle_for(OrderWorkflow.run, f"order-{order_id}").signal(OrderWorkflow.approve)
    return jsonify(ok=True)


@app.post("/address/<order_id>")
async def address(order_id: str):
    body = await request.get_json()
    handle = client.get_workflow_handle_for(OrderWorkflow.run, f"order-{order_id}")
    try:
        await handle.execute_update(OrderWorkflow.change_address, body["address"])
    except WorkflowUpdateFailedError:
        return jsonify(ok=False, message="Rejected: address is too short"), 400
    return jsonify(ok=True, message="Address updated")


if __name__ == "__main__":
    app.run(port=5050)
