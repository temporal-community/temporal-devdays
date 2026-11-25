# Exercise 01: Hello, durable world

Run your first Workflow: it calls one Activity and returns a greeting.

[Concept](../../concepts/01-hello.md) · Slides: `demo-first, write-activity, write-workflow, init-worker`

## Run the check
```bash
temporal server start-dev      # only needed for the live run below
cd python/exercise_01_hello
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`worker.py`): create the Worker for `TASK_QUEUE` with the Workflow and the Activity
- [ ] TODO 2 (`workflows.py`): call `create_greeting` with `workflow.execute_activity` and return the result

## The check
The Workflow returns `Hello, Temporal!`.

## Run it live
```bash
uv run python worker.py      # terminal 2
uv run python starter.py     # terminal 3
```

## Stretch
Change the greeting, restart the Worker and run the starter again. Find the run in the Web UI at http://localhost:8233.

Solution: [solution_01_hello](../solution_01_hello)
