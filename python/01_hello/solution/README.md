# Solution 01: Hello, durable world

Complete code for [exercise](../exercise). Concept: [01-hello.md](../../../concepts/01-hello.md).

## What changed
TODO 1 registers the Workflow and the Activity on the Worker. TODO 2 calls the Activity from the Workflow. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The Worker is what runs your code. The Workflow decides the steps, and `execute_activity` hands the real work to the Activity.

## Run the tests
```bash
cd python/01_hello/solution
uv run pytest
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `python/01_hello/solution`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
You should see `Hello, Temporal!`.
