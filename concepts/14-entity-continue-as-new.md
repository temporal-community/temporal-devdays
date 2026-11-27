# 14: Entity Workflows and Continue-As-New

## The problem
A shopping cart lives for weeks and gets thousands of updates. If one Workflow records every update in its history, the history grows without limit and eventually hits the limit.

## The idea
An **Entity Workflow** is one long-lived Workflow that stands for one thing (a cart, an account) and receives messages for it. To keep its history small, it uses **Continue-As-New**: it ends the current run and starts a fresh run with the same Workflow ID, passing along the state it needs. From the outside it is still the same cart.

## Picture
```
run 1: items 1..10 ──▶ continue as new (state: 10 items)
run 2: items 11..20 ─▶ continue as new (state: 20 items)
run 3: ...
same Workflow ID, short history in every run
```

## How it works
- A history warns at 10,240 events or 10 MB and is stopped at 51,200 events or 50 MB. A Workflow that loops for ever must start a new run before that.
- **Continue-As-New** closes the current run and starts a new run with the same Workflow ID, a fresh history, and the arguments you pass. Messages sent to the ID reach the new run.
- The SDK tells you when to do it (`is_continue_as_new_suggested` in Python, `isContinueAsNewSuggested` in Java), or you do it every N events.
- Before continuing, finish handling any messages that are being processed, and pass along anything not yet handled, so none are lost.

## Production use cases
- **Shopping carts and accounts**: one Workflow per entity, living for weeks.
- **Per-device or per-user state machines**: a device Workflow receiving telemetry Signals.
- **Polling loops and subscriptions**: check something every hour for ever, with a short history each run.
- **Rate limiters or counters** that must hold state over a long time.

## Design decisions
- **When to continue**: by size (the SDK's suggestion), or on a natural boundary such as every 1,000 messages or once a day.
- **What to carry**: the whole state the next run needs, kept small. Store large data elsewhere and carry a reference.
- **Pending messages**: drain the handlers first, or pass unhandled items forward.
- **One Workflow per entity, with the business ID**: this ties in with concept 11.
- **Search and visibility**: the Workflow ID stays the same across runs, so use it to find the entity. Each run has its own Run ID.

## Failure modes
- **No Continue-As-New**: the Workflow reaches the history limit and is terminated.
- **State not carried over**: the cart forgets its items.
- **Messages lost at the boundary**: a Signal arrives while you continue and is not passed on. Drain first.
- **Large state in the arguments**: payload limit (2 MB) and slow replays. Pass references.

## What to measure
- History length of long-running Workflows, and how close they get to the limits.
- Runs per Workflow ID (how often it continues).
- Size of the state you carry.

## Use it when
- A Workflow runs for a long time or handles many messages.
- One Workflow stands for one business object.

## Don't use it when
- The Workflow is short. It finishes long before the history is a problem.

## Common mistake
Carrying too little state into the new run, or none, so the cart forgets its items. Pass the whole state the next run needs as the argument.

## Learn more
- [Continue-As-New](https://docs.temporal.io/workflow-execution/continue-as-new)
- [Python: Continue-As-New](https://docs.temporal.io/develop/python/continue-as-new)
- [Java: Continue-As-New](https://docs.temporal.io/develop/java/continue-as-new)
- [Event History](https://docs.temporal.io/encyclopedia/event-history)
