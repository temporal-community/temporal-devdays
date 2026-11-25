# Solution 13: Fan-out, fan-in and child Workflows

Complete code for [the exercise](../exercise). Concept: [13-fan-out-fan-in.md](../../../concepts/13-fan-out-fan-in.md).

## What changed
TODO 1 starts all the Activities before waiting. TODO 2 moves shipping into a child Workflow. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Starting every Activity before waiting lets them run together. A child Workflow gets its own ID and history.

## Run
```bash
cd python/13_fan_out_fan_in/solution
uv run pytest
```
