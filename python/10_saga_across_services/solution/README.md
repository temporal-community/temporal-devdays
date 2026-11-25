# Solution 10: Saga across services

Complete code for [the exercise](../exercise). Concept: [10-saga-across-services.md](../../../concepts/10-saga-across-services.md).

## What changed
TODO 1 notes the undo before the risky step. TODO 2 runs the notes when something fails. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
A step can land and then fail. If the undo was only noted after the step, it is lost. Noting it first means it always runs.

## Run
```bash
cd python/10_saga_across_services/solution
uv run pytest
```
