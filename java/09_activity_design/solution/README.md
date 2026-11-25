# Solution 09: Activity design

Complete code for [the exercise](../exercise). Concept: [09-activity-design.md](../../../concepts/09-activity-design.md).

## What changed
TODO 1 replaces the Activity with an `if`. TODO 2 splits one Activity into two. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Plain logic needs no Activity. With one Activity per outside call, a retry repeats only the step that failed.

## Run
```bash
cd java/09_activity_design/solution
mvn test
```
