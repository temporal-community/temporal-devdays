# Exercise 06: Schedules

Create a Schedule that starts a Workflow every minute.

[Concept](../../../concepts/06-schedules.md) · Slides: `sched-code, demo-sched`

## Run the check
```bash
cd python/06_schedules/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`schedules.py`): `build_schedule`: start `ReportWorkflow` (id `report`, task queue `reports`) every minute
- [ ] TODO 2 (`schedules.py`): `create_schedule`: create it with `client.create_schedule`

## The check
The Schedule starts `ReportWorkflow` on the `reports` queue with id `report` every minute, and it is created as `nightly-report`.

## Run it live
```bash
uv run python worker.py      # terminal 2
uv run python create.py     # terminal 3
```

Then: `temporal schedule trigger --schedule-id nightly-report`, `temporal schedule list`, and open the Schedules page in the Web UI. Delete it with `temporal schedule delete --schedule-id nightly-report`.

## Stretch
Pause the Schedule from the Web UI, then trigger it by hand.

Solution: [solution](../solution)
