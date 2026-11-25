# Solution 12: Human in the loop

Complete code for [the exercise](../exercise). Concept: [12-human-in-the-loop.md](../../../concepts/12-human-in-the-loop.md).

## What changed
TODO 1 waits with a timeout. TODO 2 stores the approver. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
The wait is a durable Timer raced against the Signal, so the Workflow continues on whichever comes first.

## Run
```bash
cd java/12_human_in_the_loop/solution
mvn test
```
