import uuid

from temporalio.client import Client

from workflows import OrderWorkflow, TransferWorkflow

TASK_QUEUE = "orders"


async def start_transfer(client: Client, transfer_id: str):
    # TODO 1 start: start TransferWorkflow with the business id f"transfer-{transfer_id}" as the Workflow ID
    return await client.start_workflow(
        TransferWorkflow.run, id=f"transfer-{transfer_id}", task_queue=TASK_QUEUE)
    #? return await client.start_workflow(
    #?     TransferWorkflow.run, id=str(uuid.uuid4()), task_queue=TASK_QUEUE)
    # TODO 1 end


async def add_item(client: Client, order_id: str, item: str):
    # TODO 2 start: start the order and add the item in one call with start_signal and start_signal_args
    return await client.start_workflow(
        OrderWorkflow.run,
        id=f"order-{order_id}",
        task_queue=TASK_QUEUE,
        start_signal="add_item",
        start_signal_args=[item],
    )
    #? handle = await client.start_workflow(
    #?     OrderWorkflow.run, id=f"order-{order_id}", task_queue=TASK_QUEUE)
    #? await handle.signal(OrderWorkflow.add_item, item)
    #? return handle
    # TODO 2 end
