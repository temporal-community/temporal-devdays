# Exercise 13: Fan-out, fan-in and child Workflows

Make the items of an order in parallel, and ship the order in a child Workflow.

[Concept](../../../concepts/13-fan-out-fan-in.md) · Slides: `child-code`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd python/13_fan_out_fan_in/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`workflows.py`): start `make_item` for every item first, then wait with `asyncio.gather`
- [ ] TODO 2 (`workflows.py`): ship the order in a child Workflow with the id `ship-<order id>`

## The check
More than one item is made at once, and the history shows a child Workflow with the id `ship-o1`.

## Run it live
No live run for this topic: the check is the exercise.

## Stretch
Add a fourth item and check that the time barely changes.

Solution: [solution](../solution)
