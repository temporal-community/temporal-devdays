# Solution 07: Standalone Activities

Complete code for [the exercise](../exercise). Concept: [07-standalone-activities.md](../../../concepts/07-standalone-activities.md).

## What changed
TODO 1 runs the Activity right away and waits for the result. TODO 2 starts it with a delay and returns a handle. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
A client call starts the Activity directly. The Worker runs it like any Activity, with retries and timeouts, and the delay postpones the first attempt.

## Run
```bash
cd python/07_standalone_activities/solution
uv run pytest
```
