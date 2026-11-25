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
