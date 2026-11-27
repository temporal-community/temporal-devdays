# 06: Schedules

## The problem
You need a report every night. A cron job on one server works until that server is down, and then nobody knows the report was missed. And when the report takes longer than expected, the next night's run starts on top of it.

## The idea
A **Schedule** is a recipe stored in Temporal: "start this Workflow every so often". Temporal starts a normal Workflow run each time, so you get retries, history and a place to look in the Web UI. You can pause, trigger and delete a Schedule without redeploying code.

## Picture
```
Schedule "nightly-report"  ── every minute ──▶  Workflow run 1, run 2, run 3 ...
(stored in Temporal)                            each one visible in the Web UI
```

## How it works
- A Schedule has a **spec** (when: an interval, a calendar, or a cron expression) and an **action** (what: start this Workflow with these arguments).
- Each firing starts a new Workflow run with a generated ID (your ID plus the time), so runs never collide.
- The **overlap policy** says what to do if the last run is still going. The default is to skip the new one. Other choices include buffering it or cancelling the old one.
- The **catch-up window** decides what happens to runs missed while Temporal or your Workers were down: they are started when service resumes, if they are inside the window.
- You can **trigger** one run now, **backfill** past times, **pause**, **update** or **delete**, all with the CLI, the Web UI or code.

## Pause, delete, or let it end
- **Pause** keeps the Schedule but stops new runs. Use it for a planned break.
- **Delete** removes the Schedule. Runs already started finish on their own.
- **Limit** the number of runs (`limited_actions` in Python, `setLimitedAction` in Java) for a demo, so a forgotten Schedule ends by itself.

A Schedule keeps starting Workflows until you stop it, even after you close your terminal. Delete demo Schedules when you are done.

## Production use cases
- **Nightly reports and exports**: a calendar spec in the customer's time zone.
- **Hourly syncs with another system**: an interval spec, with the overlap policy set to skip so a slow sync does not pile up.
- **Housekeeping**: clean up expired data, renew certificates, send digest emails.
- **Rollout control**: pause a Schedule during an incident without a deploy.

## Design decisions
- **Schedule or Timer?** A Schedule starts new Workflows on a calendar. A Timer waits inside one Workflow.
- **Overlap policy**: pick it on purpose. Skip suits "latest wins" jobs. Buffering suits "every run must happen" jobs.
- **Make the Workflow idempotent**: a run can be triggered, backfilled or retried, so the same time window may run twice.
- **Time zones and daylight saving**: use a calendar spec with a time zone, not a UTC interval, for "9 am local".
- **Run duration**: if a run can outlast its interval, set a Workflow timeout and expect skips.
- **Own the lifecycle**: someone must be responsible for deleting Schedules that are no longer needed.

## Failure modes
- **Forgotten demo Schedule**: it keeps creating Workflows for ever. Delete it, or limit its runs.
- **No Worker for the queue**: runs start and wait. The Schedule shows runs, the Workflows do nothing.
- **Overlap**: slow runs get skipped without anyone noticing unless you watch for it.
- **Paused and forgotten**: pause is invisible unless you check the Schedules list.

## What to measure
- Runs started versus runs expected, and the number skipped by the overlap policy.
- Duration of the scheduled Workflow against its interval.
- Number of paused Schedules, reviewed regularly.

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
