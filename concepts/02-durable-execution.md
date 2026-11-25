# 02: Durable execution

## The problem
A customer orders a pizza. The app waits 30 minutes, then sends the bill. If the server restarts at minute 20, a normal program forgets the order and the bill never goes out.

## The idea
Temporal writes every step of your Workflow into a log called the **Event History**. If the Worker dies, another Worker reads the log and replays your code until it reaches the same point, without redoing finished steps. A **Timer** is a wait that lives in Temporal, not in your process, so it survives restarts.

## Picture
```
Worker A:  take_order ✓ ── Timer started ── 💥 crash
Worker B:  replay history ── Timer still running ── bake ✓
```

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
