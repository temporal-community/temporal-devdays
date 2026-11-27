# Solution 12: Human in the loop

Complete code for [the exercise](../exercise). Concept: [12-human-in-the-loop.md](../../../concepts/12-human-in-the-loop.md).

## What changed
TODO 1 waits with a timeout. TODO 2 stores the approver. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
The wait is a durable Timer raced against the Signal, so the Workflow continues on whichever comes first.

## Run the tests
```bash
cd java/12_human_in_the_loop/solution
mvn test
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `java/12_human_in_the_loop/solution`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
The starter shows the approval Workflow waits, then a Signal approves it. You should see `approved by alice`.
