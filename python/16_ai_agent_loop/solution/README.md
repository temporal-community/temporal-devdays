# Solution 16: An AI agent loop

Complete code for [the exercise](../exercise). Concept: [16-ai-agent-loop.md](../../../concepts/16-ai-agent-loop.md).

## What changed
TODO 1 turns the model call into an Activity with a Retry Policy. TODO 2 turns the tool call into an Activity. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Activity results are recorded, so a crash does not repeat a paid model call, and the Retry Policy handles rate limits in one place.

## Run the tests
```bash
cd python/16_ai_agent_loop/solution
uv run pytest
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `python/16_ai_agent_loop/solution`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
You should see `It is sunny in Pune`.
