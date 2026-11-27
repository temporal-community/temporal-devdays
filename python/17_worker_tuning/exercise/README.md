# Exercise 17: Tune a Worker

Set two limits on a Worker, so it never runs too many Activities at once and never starts them too fast.

[Concept](../../../concepts/17-worker-tuning.md)

## Run the check
```bash
cd python/17_worker_tuning/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`tuning.py`): `max_slots`: return 3, the most Activities this Worker may run at once
- [ ] TODO 2 (`tuning.py`): `max_per_second`: return 5, the most Activities this Worker may start per second

## The check
With 12 items, at most 3 Activities run at the same time. With a limit of 5 starts per second, the same 12 items take longer than 1.8 seconds.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `python/17_worker_tuning/exercise`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py      # terminal 3
```
The starter renders 30 items. With 5 starts per second it takes about 5 seconds. Open Task Queues in the Web UI at http://localhost:8233 and watch the `render` queue drain. Then change the numbers in `tuning.py`, restart the Worker and run the starter again. Compare the time.

## Stretch
The server can also limit a whole task queue, across all Workers: `max_task_queue_activities_per_second` (Python) or `setMaxTaskQueueActivitiesPerSecond` (Java). The in-process test server does not apply it, so try it against the dev server and compare with the per-Worker limit. Then run two Workers and see what each limit does.

Solution: [solution](../solution)
