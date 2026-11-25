# Solution 06: Schedules

Complete code for [the exercise](../exercise). Concept: [06-schedules.md](../../../concepts/06-schedules.md).

## What changed
TODO 1 describes what to run and how often. TODO 2 sends it to Temporal. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The Schedule lives in Temporal. Each tick starts a normal Workflow run, so you can read it in the Web UI like any other.

## Run
```bash
cd python/06_schedules/solution
uv run pytest
```
