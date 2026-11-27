# Solution 09: Activity design

Complete code for [the exercise](../exercise). Concept: [09-activity-design.md](../../../concepts/09-activity-design.md).

## What changed
TODO 1 replaces the Activity with an `if`. TODO 2 splits one Activity into two. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Plain logic needs no Activity. With one Activity per outside call, a retry repeats only the step that failed.

## Run the tests
```bash
cd java/09_activity_design/solution
mvn test
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `java/09_activity_design/solution`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
The starter shows a claim is decided in the Workflow, then a user is onboarded. You should see `claim decision: review`.
