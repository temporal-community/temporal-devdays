# 02: Durable execution

## The problem
A customer orders a pizza. The app waits 30 minutes, then sends the bill. If the server restarts at minute 20, a normal program forgets the order and the bill never goes out. The same shape appears everywhere: a trial that ends in 14 days, a payment reminder in 3 days, a contract that expires in a year.

## The idea
Temporal writes every step of your Workflow into a log called the **Event History**. If the Worker dies, another Worker reads the log and replays your code until it reaches the same point, without redoing finished steps. A **Timer** is a wait that lives in Temporal, not in your process, so it survives restarts.

## Picture
```
Worker A:  take_order ✓ ── Timer started ── 💥 crash
Worker B:  replay history ── Timer still running ── bake ✓
```

## How it works
- Each time your Workflow does something that matters (schedules an Activity, starts a Timer), Temporal appends an event: `ActivityTaskScheduled`, `ActivityTaskCompleted`, `TimerStarted`, `TimerFired`.
- When a Worker takes over, it runs your Workflow function from the top. At each step the SDK compares what your code does with the next event in the history. If they match, it uses the recorded result instead of doing the work again.
- Where the history ends, your code is now doing something new, so the Worker records new events and continues.
- A Timer is just an event pair in the history. While it runs, no Worker holds your Workflow in memory. Thousands of waiting Workflows cost almost nothing.

## Production use cases
- **Trial and subscription lifecycles**: wait 14 days, then convert or cancel. One Workflow per subscription.
- **Reminders and escalations**: wait 3 days for payment, then send a reminder, then wait 7 more and escalate.
- **Long approvals and cool-off periods**: hold a refund for 24 hours so fraud checks can finish.

These are the jobs that otherwise need a database of "run at" rows plus a cron job to poll it.

## Design decisions
- **Timer or Schedule?** A Timer is a wait *inside* one Workflow. A Schedule starts new Workflows on a calendar (concept 06).
- **Long waits**: a Timer can be days or months. Remember the history grows with every event, so a Workflow that loops forever should use Continue-As-New (concept 14).
- **Code changes while Workflows are waiting**: replay runs your *new* code against the *old* history. Changing the order of steps can break running Workflows. Concept 15 shows the safe way.

## Failure modes
- **Worker crash mid-run**: nothing to do. Start a Worker again and the Workflow carries on from its last recorded step.
- **Non-determinism error**: your new code does something the history does not expect, so replay stops. The Workflow is blocked, not lost. Fix the code or revert and restart the Worker.
- **Using the language's own sleep, clock or random**: these give a different answer on replay. Use the Workflow's own versions.

## What to measure
- How many Workflows are Running and for how long, per Workflow type (Web UI list view or a Visibility query).
- Workflow task failures. A rise after a deploy is the sign of a non-determinism error.

## Use it when
- A process waits for minutes, days or months.
- You cannot lose a half-finished job.

## Don't use it when
- The work takes milliseconds and can simply be retried from the start.

## Common mistake
Using `sleep` from your language instead of the Workflow's own sleep. Use `asyncio.sleep` inside Python Workflows and `Workflow.sleep` in Java.

## Learn more
- [Event History](https://docs.temporal.io/encyclopedia/event-history)
- [Python: timers](https://docs.temporal.io/develop/python/timers)
- [Java: timers](https://docs.temporal.io/develop/java/timers)
- [Workflows](https://docs.temporal.io/workflows)
