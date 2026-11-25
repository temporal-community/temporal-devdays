# Exercise 10: Saga across services

Make a transfer across three services undo its withdrawal when a later step fails.

[Concept](../../../concepts/10-saga-across-services.md) · Slides: `saga-code, tr-1`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd python/10_saga_across_services/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`workflows.py`): add `undo_withdraw` to the undo list BEFORE calling `withdraw`
- [ ] TODO 2 (`workflows.py`): in the `except`, run the undo steps in reverse order

## The check
A failed deposit undoes the withdrawal, a fraud failure has nothing to undo, and a withdrawal that lands and then fails is still undone.

## Run it live
Run the four services, each in its own terminal, then start a transfer:
```bash
uv run python worker.py orchestrator
uv run python worker.py accounts
uv run python worker.py fraud
uv run python worker.py notifications
uv run python starter.py            # succeeds
uv run python starter.py deposit    # fails at deposit, the withdrawal is undone
uv run python starter.py withdraw   # fails after it landed, still undone
```

## Stretch
Add a second undo for `deposit` and check the undos run newest first.

Solution: [solution](../solution)
