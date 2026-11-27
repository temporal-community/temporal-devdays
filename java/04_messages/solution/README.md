# Solution 04: Signals, Queries and Updates

Complete code for [exercise](../exercise). Concept: [04-messages.md](../../../concepts/04-messages.md).

## What changed
TODO 1 to 4 fill in the Signal, Query, Update and Update validator bodies. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The Signal flips the flag the Workflow waits on, the Query only reads, and the Update changes state and answers after the validator accepts the input.

## Run the tests
```bash
cd java/04_messages/solution
mvn test
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `java/04_messages/solution`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
You should see `Started transfer-1`.

When you are done, stop it: `temporal workflow terminate --workflow-id transfer-1`
