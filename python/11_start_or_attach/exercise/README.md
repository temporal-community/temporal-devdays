# Exercise 11: Start or attach

Stop double starts with a business Workflow ID, and add items to a running order with one call.

[Concept](../../../concepts/11-start-or-attach.md) · Slides: `signal-with-start`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd python/11_start_or_attach/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`orders.py`): `start_transfer`: use `transfer-<id>` as the Workflow ID
- [ ] TODO 2 (`orders.py`): `add_item`: use `start_signal` so one call starts or attaches

## The check
A second start of the same transfer is rejected, and two `add_item` calls on one order give one Workflow holding both items.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `python/11_start_or_attach/exercise`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
The starter shows a second start of the same transfer is rejected, and two `add_item` calls reach one order. You should see `second start rejected`.

## Stretch
Start the same transfer a third time and read the error you get.

Solution: [solution](../solution)
