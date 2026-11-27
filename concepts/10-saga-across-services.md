# 10: Saga across services

## The problem
A money transfer touches three services: fraud check, accounts, notifications. If the deposit fails after the withdrawal worked, the money has left one account and arrived nowhere. There is no database transaction that spans services.

## The idea
A **Saga** is a list of undo steps. Before each step that changes something, you note how to undo it. If a later step fails, you run the undo steps in reverse order. The Workflow acts as the **orchestrator**: it calls each service in turn, and each service runs its own Worker on its own Task Queue.

## Picture
```
check_fraud → [note undo] withdraw → deposit ✗
                                      │
                     undo_withdraw ◀──┘   (runs the notes in reverse)
```

## How it works
- The Workflow keeps a list of undo steps. After a step that changes something, it adds the matching undo to the list.
- When a later step fails with a final error, the Workflow runs the list in reverse, calling each undo as an Activity on the right service.
- The Workflow itself is durable, so a crash in the middle of undoing does not lose the list. It carries on from where it was.
- Each service is a Worker on its own Task Queue. The Workflow chooses the queue per call, so services never call each other.

## Production use cases
- **Money movement**: withdraw, deposit, notify. If deposit fails, return the withdrawal.
- **Order fulfilment**: reserve stock, charge the card, ship. A failed shipment refunds the card and releases the stock, in that order (see `demos/order-management`).
- **Travel booking**: flight, hotel and car. If the car fails, cancel the hotel and the flight.

## Design decisions
- **When to note the undo**: note it *before* the step when the step might take effect even though the call fails or the reply is lost (a withdrawal). Note it *after* the step when a failure proves nothing happened (a card declined with no money moved). Make every undo safe to run when there is nothing to undo.
- **The order demo shows this**: it registers `refund_charge` before the charge, and the refund checks whether a charge exists. A declined card runs the refund, which finds nothing and does nothing.
- **Order**: put steps that cannot be undone (an email already sent) last.
- **Retry the undo**: undo steps are Activities with a Retry Policy, and they should retry until they succeed, because a half-undone saga is the worst state.
- **Failed undo**: decide what happens when an undo itself cannot finish: alert a person, and keep the Workflow open for them.
- **Compensation is not a rollback**: the outside world saw the first step. A refund is a new event, not an erase.

## Failure modes
- **Undo not safe to repeat**: a refund issued twice.
- **Undo noted only after the step and the step landed despite an error**: money out, no undo.
- **Undo steps run in the wrong order**: release the stock before refunding the card and a retry can lose track.
- **No one watching failed sagas**: a Workflow stuck on a failing undo needs an alert.

## What to measure
- Sagas started versus compensated versus failed to compensate.
- Time spent in undo steps and the number of undo retries.
- Open Workflows that are in a compensation step for a long time.

## Use it when
- A business action spans several services and each step can be undone.
- You need a clear "money returned" outcome when something fails midway.

## Don't use it when
- A step cannot be undone (an email already sent). Order the steps so those come last.

## Common mistake
Noting the undo after a step that might have taken effect. If the step works on the server but the reply is lost, or it fails after it landed, the undo was never noted and never runs. Note the undo first for such steps, and make each undo safe to run when there is nothing to undo.

## Learn more
- [Saga pattern](https://docs.temporal.io/design-patterns/saga-pattern)
- [Design patterns](https://docs.temporal.io/design-patterns)
- [Activities](https://docs.temporal.io/activities)
- [Task Queues](https://docs.temporal.io/task-queue)
