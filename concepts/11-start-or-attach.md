# 11: Start or attach

## The problem
A customer double-taps "Send money". Or an app retries a request after a bad connection. You now have two transfers instead of one.

## The idea
Every Workflow has an ID, and two running Workflows can never share one. Use the **business ID** (the transfer's own number) as the Workflow ID, and a second start is rejected instead of creating a duplicate. When you want "start it if it is not running, otherwise just talk to it", use **Signal-With-Start**: one call that starts the Workflow if needed and sends the Signal either way.

## Picture
```
start "transfer-42"  ──▶ started
start "transfer-42"  ──▶ rejected: already running
add_item to "order-7" ──▶ starts the order + adds item
add_item to "order-7" ──▶ order already running: just adds the item
```

## How it works
- The Workflow ID is the unique key among running Workflows in a namespace. Starting with an ID that is already running is refused by default.
- Two policies refine this. The **ID reuse policy** says whether a finished Workflow's ID can be used again. The **ID conflict policy** says what to do if one with that ID is running: fail, or attach to it.
- **Signal-With-Start** sends the Signal and starts the Workflow if it is not running, in one call, so there is no gap between "check" and "start".
- Retries by clients are safe, because the same ID cannot create a second Workflow.

## Production use cases
- **Payments and transfers**: the transfer ID is the Workflow ID, so a double-tap or a retried request never moves money twice.
- **Shopping carts**: every "add item" is a Signal-With-Start to `cart-<customer>`, so the first one creates the cart.
- **Per-customer or per-device processes**: one long-lived Workflow per entity, found by its natural ID.

## Design decisions
- **ID from the business**: a transfer number, an order ID, a customer ID. A random UUID gives you no protection.
- **After it finishes**: choose whether the same ID may run again. A one-time payment should not. A daily job per customer can.
- **Attach or reject?** Reject when a second request is a mistake. Attach (Signal-With-Start) when it is an addition to the same thing.
- **Return the existing result**: when a caller is told "already running", it can wait on or query the existing Workflow.
- **One ID, one meaning**: do not reuse an ID format for two different kinds of work.

## Failure modes
- **Random IDs**: duplicates on every retry.
- **IDs that are not unique enough**: two customers collide on `cart-1`.
- **Signal sent before the Workflow exists**: lost, unless you use Signal-With-Start.
- **Expecting a rejected start to be an error in your API**: handle it and return the existing item.

## What to measure
- Rejected starts (these are duplicates you avoided).
- Signals per Workflow and history length of entity Workflows (see concept 14).

## Use it when
- A business item (an order, a transfer) must exist once.
- Several callers add to the same running Workflow.

## Don't use it when
- You really want a new, independent run every time. Then a unique ID is right.

## Common mistake
Using a random ID (a UUID) by default. It looks safe, but every retry or double click starts a new Workflow. Use the ID the business already has.

## Learn more
- [Workflow ID and Run ID](https://docs.temporal.io/workflow-execution/workflowid-runid)
- [Sending messages](https://docs.temporal.io/sending-messages)
- [Python: message passing](https://docs.temporal.io/develop/python/message-passing)
- [Java: message passing](https://docs.temporal.io/develop/java/message-passing)
