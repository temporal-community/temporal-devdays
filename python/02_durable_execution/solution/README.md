# Solution 02: Durable execution

Complete code for [exercise](../exercise). Concept: [02-durable-execution.md](../../../concepts/02-durable-execution.md).

## What changed
TODO 1 adds the Timer between the two Activities. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The Timer is stored by Temporal, not by your process, so a restarted Worker replays the history and keeps waiting without running `take_order` again.

## Run the tests
```bash
cd python/02_durable_execution/solution
uv run pytest
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `python/02_durable_execution/solution`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
You should see `Started order-1`.

When you are done, stop it: `temporal workflow terminate --workflow-id order-1`
