# Exercise 07: Standalone Activities

Run one Activity with no Workflow around it, now and later.

[Concept](../../../concepts/07-standalone-activities.md) · Slides: `sa-define, sa-run, sa-delay, demo-sa`

## Run the check
```bash
cd python/07_standalone_activities/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`notifications.py`): `run_now`: run `send_notification` with `client.execute_activity` and return the result
- [ ] TODO 2 (`notifications.py`): `run_later`: start it with `client.start_activity`, `start_delay=delay` (6 hours by default) and `id=activity_id`

## The check
`run_now` / `runNow` runs the Activity with the right id, queue and timeout, and `run_later` / `runLater` adds a 6 hour start delay.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `python/07_standalone_activities/exercise`:
```bash
uv run python worker.py      # terminal 2
uv run python run_standalone.py     # terminal 3
```

You should see the first message at once, then `scheduled, runs in 10 seconds`, then the second message after 10 seconds. Then run `temporal activity list` and open the Standalone Activities view in the Web UI. Both Activities are there, with the IDs `welcome-notification-user-123` and `welcome-notification-user-123-later`.

## Stretch
Call the same Activity from a Workflow instead (`execute_activity` / `executeActivity` with the same Activity and no change to it). It is the same code in a bigger plan, which is how a one-step job grows. Then change the message, run it again with a new id, and compare in `temporal activity list`.

Solution: [solution](../solution)
