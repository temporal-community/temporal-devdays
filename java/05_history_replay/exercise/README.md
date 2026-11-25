# Exercise 05: Event History and replay

Read an Event History, replay it against your code, and see replay fail when the code changes.

[Concept](../../../concepts/05-history-replay.md) · Slides: `demo-history, eh-structure, demo-replay`

## Run the check
```bash
cd java/05_history_replay/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`Replay.java`): `eventTypes`: return the name of every event, without the `EVENT_TYPE_` prefix
- [ ] TODO 2 (`Replay.java`): `replay`: replay the history with `WorkflowReplayer`

## The check
The history starts with `WORKFLOW_EXECUTION_STARTED` and contains a Timer, the same code replays, and the Workflow with the Timer moved (`swapped_workflow.py` / `SwappedPizzaWorkflowImpl.java`) fails to replay.

## Run it live
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter     # terminal 3
```

Then read the history of the run: `temporal workflow show --workflow-id order-1` and the same run in the Web UI.

## Stretch
Open the swapped Workflow next to the original and find the one change that breaks replay.

Solution: [solution](../solution)
