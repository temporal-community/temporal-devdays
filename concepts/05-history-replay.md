# 05: History and replay

## The problem
A Workflow ran for two days and then your Worker was replaced. How does the new Worker know where the old one stopped? And how can you be sure a code change will not break Workflows that are still running?

## The idea
Every step a Workflow takes is written to an **Event History**, a log you can read. To continue a Workflow, a Worker **replays** it: it runs your code again from the top and checks each step against the history. Finished steps return the recorded result instead of running again. If your code now does something different from the history, replay fails, and that is how you catch a risky change before it ships.

## Picture
```
history:  Started → ActivityScheduled → ActivityCompleted → TimerStarted → ...
code:     get_distance()  →  sleep()  →  send_bill()
replay:   each line must match the next event in the log
```

## How it works
- The history is an ordered list of events: Workflow started, Activity scheduled, Activity completed, Timer started, Signal received, and so on. You can read it in the Web UI or with `temporal workflow show`.
- Replay re-runs only your **Workflow** code. Activities are not run again, because their results are already in the history.
- For each command your code issues ("schedule this Activity", "start this Timer"), the SDK checks it against the next event. A mismatch is a **non-determinism error**.
- Workers keep recently used Workflows in a cache, so they do not replay on every step. Replay happens when a Workflow is not in the cache: after a restart, an eviction, or when another Worker takes over.
- You can export a history to a file and replay it in a test against your new code. No server is needed.

## Production use cases
- **Safe deploys**: before releasing, replay histories exported from production against the new code in CI. A failing replay means a running Workflow would break.
- **Debugging**: read the history of one stuck order to see the exact last step, the error, and how many attempts ran.
- **Audit and support**: the history is a complete record of what the Workflow did and when, with no extra logging.

## Design decisions
- **Keep Workflow code deterministic**: no direct clock, random numbers, network, files, threads or global state. Use the Workflow's own time, random and Activities.
- **How many histories to replay in CI?** A few real ones per Workflow type, including long-running and failed ones, give most of the value.
- **Changing running code**: some edits are safe (Activity code, logging), some are not (order of steps, adding or removing a call). Concept 15 covers patching and versioning.
- **Export histories from the CLI**: `temporal workflow show --workflow-id <id> --output json` gives a file the replay test can read.

## Failure modes
- **Non-determinism error after a deploy**: the Workflow is blocked, not lost. Revert the code or fix it, restart the Worker, and the Workflow continues.
- **Replay passes in a test with an old history but a Workflow type has a new branch**: the old history never takes the new path. Include histories that cover each branch.
- **Hidden non-determinism**: iterating a set or a map whose order changes, reading the system clock, generating a random ID in the Workflow.

## What to measure
- Workflow task failures per Workflow type. Alert on any rise after a deploy.
- Replay test pass rate in CI. It should be 100%.
- History length of long-running Workflows, so you see growth before it becomes a problem (concept 14).

## Use it when
- You want to see exactly what happened in a Workflow (the Web UI shows the history).
- You change Workflow code and want to know if running Workflows would break.

## Don't use it when
- You want to test business logic only. A normal unit test is faster.

## Common mistake
Reordering steps in a Workflow that is already running. The history says "Activity, then Timer" and the new code says "Timer, then Activity". Replay fails. Test with a recorded history before you deploy.

## Learn more
- [Event History](https://docs.temporal.io/encyclopedia/event-history)
- [Workflow definition and determinism](https://docs.temporal.io/workflow-definition)
- [Python: testing suite and replay](https://docs.temporal.io/develop/python/testing-suite)
- [Java: testing suite and replay](https://docs.temporal.io/develop/java/testing-suite)
