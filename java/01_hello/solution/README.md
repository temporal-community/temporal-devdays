# Solution 01: Hello, durable world

Complete code for [exercise](../exercise). Concept: [01-hello.md](../../../concepts/01-hello.md).

## What changed
TODO 1 registers the Workflow and the Activity on the Worker. TODO 2 calls the Activity from the Workflow. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The Worker is what runs your code. The Workflow decides the steps, and `execute_activity` hands the real work to the Activity.

## Run
```bash
cd java/01_hello/solution
mvn test
```
