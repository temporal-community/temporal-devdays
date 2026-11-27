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

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `python/15_safe_deploys/solution`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
The starter shows one order runs through the patched Workflow. You should see `baked ticket-Ana`.
