# Solution 11: Start or attach

Complete code for [the exercise](../exercise). Concept: [11-start-or-attach.md](../../../concepts/11-start-or-attach.md).

## What changed
TODO 1 swaps the random ID for the business ID. TODO 2 swaps start-then-signal for Signal-With-Start. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Two running Workflows cannot share an ID, so the business ID blocks duplicates. Signal-With-Start does the check and the Signal in one step.

## Run the tests
```bash
cd python/11_start_or_attach/solution
uv run pytest
```
No server needed.

This topic is checked with tests only. There is no live run.
