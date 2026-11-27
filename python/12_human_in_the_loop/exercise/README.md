# Exercise 12: Human in the loop

Wait for a person's approval, but not forever.

[Concept](../../../concepts/12-human-in-the-loop.md) · Slides: `await-code, signals-define`

## Run the check
```bash
cd python/12_human_in_the_loop/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`workflows.py`): wait for `self.approver` with `wait_condition` and a 24 hour timeout, return `timed out` on timeout
- [ ] TODO 2 (`workflows.py`): `approve`: remember who approved

## The check
After `approve("alice")` the result is `approved by alice`. With no answer the result is `timed out`.

## Run it live
No live run for this topic: the check is the exercise.

## Stretch
Change the timeout to 1 hour and see that the test still finishes at once.

Solution: [solution](../solution)
