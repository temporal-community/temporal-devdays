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
