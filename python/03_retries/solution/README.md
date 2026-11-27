# Solution 03: Retries and timeouts

Complete code for [exercise](../exercise). Concept: [03-retries.md](../../../concepts/03-retries.md).

## What changed
TODO 1 sets the Retry Policy. TODO 2 marks the invalid card as non-retryable. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The policy caps the retries. A non-retryable error tells Temporal to stop now, so a final error does not retry.

## Run the tests
```bash
cd python/03_retries/solution
uv run pytest
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `python/03_retries/solution`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
You should see `charged on attempt 3`.
