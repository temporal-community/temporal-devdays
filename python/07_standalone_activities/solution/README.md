# Solution 07: Standalone Activities

Complete code for [the exercise](../exercise). Concept: [07-standalone-activities.md](../../../concepts/07-standalone-activities.md).

## What changed
TODO 1 runs the Activity right away and waits for the result. TODO 2 starts it with a delay and returns a handle. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
A client call starts the Activity directly. The Worker runs it like any Activity, with retries and timeouts, and the delay postpones the first attempt.

## Run the tests
```bash
cd python/07_standalone_activities/solution
uv run pytest
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `python/07_standalone_activities/solution`:
```bash
uv run python worker.py      # terminal 2
uv run python run_standalone.py      # terminal 3
```
You should see `sent 'Welcome!' to user-123`, then `scheduled, runs in 10 seconds`, then the same message again 10 seconds later (a delayed Activity). Then run `temporal activity list`: both are there, with the IDs `welcome-notification-user-123` and `welcome-notification-user-123-later`.
