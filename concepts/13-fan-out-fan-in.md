# 13: Fan-out, fan-in and child Workflows

## The problem
An order has three pizzas. Making them one after another takes three times as long as it needs to. And shipping the order is its own little process that you want to track separately.

## The idea
**Fan-out** means starting several Activities at once. **Fan-in** means waiting until all of them are done and collecting the results. For a part of the process that deserves its own history, ID and lifecycle, start a **child Workflow**: it is a normal Workflow started by another Workflow.

## Picture
```
Kitchen Workflow ──┬─ make_item(a) ─┐
                   ├─ make_item(b) ─┼─▶ all ready ──▶ child: ShipmentWorkflow "ship-o1"
                   └─ make_item(c) ─┘
```

## How it works
- **Fan-out**: the Workflow starts several Activities without waiting for each one (`asyncio.gather` in Python, `Async.function` with `Promise.allOf` in Java). Each Activity is scheduled at once and runs on any free Worker slot.
- **Fan-in**: the Workflow waits for all the results together, then continues.
- A **child Workflow** is started by the parent, has its own Workflow ID, history and Retry Policy, and shows as its own row in the Web UI. The parent can wait for its result.
- The **parent close policy** decides what happens to a running child if the parent ends (by default it is terminated).
- How many run at once is limited by Worker capacity, not by your code (concept 17).

## Production use cases
- **Parallel line items**: ship, pick or render every item of an order at the same time.
- **Batch processing**: send 10,000 emails by giving each batch of 100 to a child Workflow, so no single history is huge.
- **Independent checks**: run fraud, address and stock checks together and combine the answers.
- **Sub-processes with their own lifecycle**: a shipment that is tracked, cancelled or retried separately from the order.

## Design decisions
- **Activity or child Workflow?** Default to an Activity. Use a child when the part needs its own history, ID, search, or a way to split a very large job.
- **Limit the width**: starting 100,000 Activities from one Workflow adds 100,000 times the events. Process in batches, or split with child Workflows.
- **All or nothing?** Decide what happens when one branch fails: fail everything, collect partial results, or undo (concept 10).
- **Results are small**: return IDs and counts, not large payloads.
- **Child IDs**: derive them from the parent's business ID (`ship-<order id>`) so they are easy to find and safe to retry.

## Failure modes
- **`await` in a loop**: runs one at a time. Start all, then wait for all.
- **Unbounded fan-out**: history size and Worker overload.
- **Child as code organisation**: extra history and complexity for nothing a function would not give.
- **One failure hides the others**: read every branch's outcome, not only the first error.

## What to measure
- Schedule-to-start latency while a big fan-out runs (are there enough Worker slots?).
- History length of the parent, and the number of running children.
- Failure rate per branch.

## Use it when
- Independent steps can run at the same time.
- A sub-process needs its own ID, history or retry behaviour.

## Don't use it when
- A child Workflow would only organise your code. A plain function does that for free. Use a child only when you need its own lifecycle.

## Common mistake
Writing `await` inside a loop. Each Activity then waits for the one before it, so nothing runs in parallel. Start them all first, then wait for all of them together.

## Learn more
- [Child Workflows](https://docs.temporal.io/child-workflows)
- [Python: child Workflows](https://docs.temporal.io/develop/python/child-workflows)
- [Java: child Workflows](https://docs.temporal.io/develop/java/child-workflows)
- [Activities](https://docs.temporal.io/activities)
