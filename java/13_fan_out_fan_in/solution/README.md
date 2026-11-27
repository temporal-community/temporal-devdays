# Solution 13: Fan-out, fan-in and child Workflows

Complete code for [the exercise](../exercise). Concept: [13-fan-out-fan-in.md](../../../concepts/13-fan-out-fan-in.md).

## What changed
TODO 1 starts all the Activities before waiting. TODO 2 moves shipping into a child Workflow. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Starting every Activity before waiting lets them run together. A child Workflow gets its own ID and history.

## Run the tests
```bash
cd java/13_fan_out_fan_in/solution
mvn test
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `java/13_fan_out_fan_in/solution`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
The starter shows three items are made in parallel and a child Workflow ships the order. You should see `shipped o1`.
