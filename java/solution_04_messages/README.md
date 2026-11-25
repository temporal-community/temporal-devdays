# Solution 04: Signals, Queries and Updates

Complete code for [exercise_04_messages](../exercise_04_messages). Concept: [04-messages.md](../../concepts/04-messages.md).

## What changed
TODO 1 to 4 fill in the Signal, Query, Update and Update validator bodies. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The Signal flips the flag the Workflow waits on, the Query only reads, and the Update changes state and answers after the validator accepts the input.

## Run
```bash
cd java/solution_04_messages
mvn test
```
