# Exercise 15: Safe deploys: patching

Add a new step to a Workflow without breaking Workflows that are already running.

[Concept](../../../concepts/15-safe-deploys.md) · Slides: `det-deploy-1, det-deploy-2, det-deploy-3`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd python/15_safe_deploys/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`workflows.py`): run `check_stock` only when `workflow.patched("check-stock")` is true

## The check
A history recorded by the old code (`history_v1.json` / `history-v1.json`) still replays, and a new run does check the stock.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `python/15_safe_deploys/exercise`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
The starter shows one order runs through the patched Workflow. You should see `baked ticket-Ana`.

## Stretch
Open `workflow_v1.py` / `OrderWorkflowV1Impl.java` and compare it with the new Workflow.

Solution: [solution](../solution)
