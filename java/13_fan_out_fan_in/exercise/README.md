# Exercise 13: Fan-out, fan-in and child Workflows

Make the items of an order in parallel, and ship the order in a child Workflow.

[Concept](../../../concepts/13-fan-out-fan-in.md) · Slides: `child-code`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd java/13_fan_out_fan_in/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`KitchenWorkflowImpl.java`): start `makeItem` for every item with `Async.function`, then wait for all the Promises
- [ ] TODO 2 (`KitchenWorkflowImpl.java`): ship the order in a child Workflow with the id `ship-<order id>`

## The check
More than one item is made at once, and the history shows a child Workflow with the id `ship-o1`.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `java/13_fan_out_fan_in/exercise`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
The starter shows three items are made in parallel and a child Workflow ships the order. You should see `shipped o1`.

## Stretch
Add a fourth item and check that the time barely changes.

Solution: [solution](../solution)
