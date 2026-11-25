# Exercise 04: Signals, Queries and Updates

Talk to a running Workflow: approve it, read its status, and change its amount.

[Concept](../../../concepts/04-messages.md) · Slides: `demo-messages, signals-define, query-code, update-code`

## Run the check
```bash
temporal server start-dev      # only needed for the live run below
cd python/04_messages/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`workflows.py`): `approve`: set `self.approved` to `True`
- [ ] TODO 2 (`workflows.py`): `get_status`: return `self.status`
- [ ] TODO 3 (`workflows.py`): `set_amount`: store the amount and return it
- [ ] TODO 4 (`workflows.py`): `check_amount`: reject an amount that is not positive

## The check
The Query returns `waiting`, the Update returns `150`, a negative Update is rejected, and after the Signal the result is `sent 150`.

## Run it live
```bash
uv run python worker.py      # terminal 2
uv run python starter.py     # terminal 3
```

From the CLI, with a Worker and the starter running: `temporal workflow signal --workflow-id transfer-1 --name approve`, `temporal workflow query --workflow-id transfer-1 --name get_status`, `temporal workflow update execute --workflow-id transfer-1 --name set_amount --input 150`.

## Stretch
Send the Update with `-5` from the CLI and read the error.

Solution: [solution](../solution)
