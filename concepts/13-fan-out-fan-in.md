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
