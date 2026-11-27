# Solution 15: Safe deploys: patching

Complete code for [the exercise](../exercise). Concept: [15-safe-deploys.md](../../../concepts/15-safe-deploys.md).

## What changed
TODO 1 guards the new step with a patch. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
The patch marks the change in the history. Old runs have no marker and skip the step, new runs record the marker and run it.

## Run the tests
```bash
cd python/15_safe_deploys/solution
uv run pytest
```
No server needed.

This topic is checked with tests only. There is no live run.
