# Solution 13: Fan-out, fan-in and child Workflows

Complete code for [the exercise](../exercise). Concept: [13-fan-out-fan-in.md](../../../concepts/13-fan-out-fan-in.md).

## What changed
TODO 1 starts all the Activities before waiting. TODO 2 moves shipping into a child Workflow. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Starting every Activity before waiting lets them run together. A child Workflow gets its own ID and history.

## Run the tests
```bash
cd python/13_fan_out_fan_in/solution
uv run pytest
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `python/13_fan_out_fan_in/solution`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
The starter shows three items are made in parallel and a child Workflow ships the order. You should see `shipped o1`.
