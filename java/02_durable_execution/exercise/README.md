# Exercise 02: Durable execution

Add a Timer to a Workflow, stop the Worker while it waits, and watch the Workflow carry on.

[Concept](../../../concepts/02-durable-execution.md) · Slides: `demo-crash`

## Run the check
```bash
temporal server start-dev      # only needed for the live run below
cd java/02_durable_execution/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`OrderWorkflowImpl.java`): wait `waitSeconds` with `Workflow.sleep`, this is a durable Timer

## The check
The result is `baked ticket-Margherita` and the Event History contains a Timer.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `java/02_durable_execution/exercise`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter     # terminal 3
```

Then try the crash: start the Worker and the starter (it waits 30 minutes), press Ctrl+C in the Worker terminal, check `temporal workflow describe --workflow-id order-1` (still Running), start the Worker again.

## Stretch
Open the Event History in the Web UI. `take_order` ran once, even though the Worker restarted.

Solution: [solution](../solution)
