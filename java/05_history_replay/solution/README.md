# Solution 05: Event History and replay

Complete code for [the exercise](../exercise). Concept: [05-history-replay.md](../../../concepts/05-history-replay.md).

## What changed
TODO 1 turns the history into a list of event names. TODO 2 replays it against the Workflow code. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
Replay runs your code again and matches each step against the recorded events. Moving the Timer changes the step order, so the match fails.

## Run the tests
```bash
cd java/05_history_replay/solution
mvn test
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `java/05_history_replay/solution`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
You should see `TPD-c1-12`.
