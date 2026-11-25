# Solution 02: Durable execution

Complete code for [exercise_02_durable_execution](../exercise_02_durable_execution). Concept: [02-durable-execution.md](../../concepts/02-durable-execution.md).

## What changed
TODO 1 adds the Timer between the two Activities. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The Timer is stored by Temporal, not by your process, so a restarted Worker replays the history and keeps waiting without running `take_order` again.

## Run
```bash
cd python/solution_02_durable_execution
uv run pytest
```
