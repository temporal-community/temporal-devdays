# Solution 05: Event History and replay

Complete code for [the exercise](../exercise). Concept: [05-history-replay.md](../../../concepts/05-history-replay.md).

## What changed
TODO 1 turns the history into a list of event names. TODO 2 replays it against the Workflow code. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
Replay runs your code again and matches each step against the recorded events. Moving the Timer changes the step order, so the match fails.

## Run the tests
```bash
cd python/05_history_replay/solution
uv run pytest
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `python/05_history_replay/solution`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
You should see `TPD-c1-12`.
