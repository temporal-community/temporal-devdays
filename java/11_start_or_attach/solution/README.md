# Solution 11: Start or attach

Complete code for [the exercise](../exercise). Concept: [11-start-or-attach.md](../../../concepts/11-start-or-attach.md).

## What changed
TODO 1 swaps the random ID for the business ID. TODO 2 swaps start-then-signal for Signal-With-Start. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Two running Workflows cannot share an ID, so the business ID blocks duplicates. Signal-With-Start does the check and the Signal in one step.

## Run
```bash
cd java/11_start_or_attach/solution
mvn test
```
