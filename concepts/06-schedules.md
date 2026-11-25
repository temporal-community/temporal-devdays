# 06: Schedules

## The problem
You need a report every night. A cron job on one server works until that server is down, and then nobody knows the report was missed.

## The idea
A **Schedule** is a recipe stored in Temporal: "start this Workflow every so often". Temporal starts a normal Workflow run each time, so you get retries, history and a place to look in the Web UI. You can pause, trigger and delete a Schedule without redeploying code.

## Picture
```
Schedule "nightly-report"  ── every minute ──▶  Workflow run 1, run 2, run 3 ...
(stored in Temporal)                            each one visible in the Web UI
```

## Use it when
- Something must run on a calendar or an interval: nightly jobs, hourly syncs.
- You want to pause or trigger it by hand.

## Don't use it when
- You only need to wait inside one Workflow ("send a reminder in 3 days"). Use a Timer there.

## Common mistake
Using a Schedule for a single delayed start. A Schedule is for repeating work. For one delayed job, start the Workflow with a start delay or use a Timer.

## Learn more
- [Schedules](https://docs.temporal.io/schedule)
- [Python: schedules](https://docs.temporal.io/develop/python/schedules)
- [Java: schedules](https://docs.temporal.io/develop/java/schedules)
- [Temporal CLI](https://docs.temporal.io/cli)
