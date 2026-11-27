# 07: Standalone Activities

## The problem
A user signs up. You want to send a welcome message now, and a reminder tomorrow. Each one is a single step: call an email service, retry if it is down, and know whether it worked. Writing a whole Workflow for one step feels like too much. Skipping Temporal means writing your own retry loop, your own "send later" job, and your own place to look when a message goes missing.

## The idea
A **Standalone Activity** runs one Activity directly from your client, with no Workflow around it. You still get retries, timeouts, a result you can wait for, and a row in the Web UI. The Activity code is the same code you would call from a Workflow, so if the job grows later you move it into a Workflow without rewriting it.

Three ways to start one:
- **Run and wait**: `execute_activity` (Python) or `execute` (Java) starts it and returns the result.
- **Start and come back**: `start_activity` or `start` returns a handle right away. Ask the handle for the result when you need it.
- **Start later**: add a `start_delay`. Temporal holds the Activity and hands it to a Worker when the delay is over.

## Picture
```
send now      client ──execute──▶ Temporal ──▶ Worker runs send_notification ──▶ result

send later    client ──start, delay 6h──▶ Temporal holds it ... 6h ... ▶ Worker runs it
              (your process can exit)                                    (you can look it up by ID)
```

## The story in this exercise
1. A user signs up. `run_now` sends the welcome message and waits for the result.
2. The same call site, `run_later`, books a reminder with a delay. The client does not have to stay alive for the delay.
3. Run `temporal activity list`. Both are there, with an ID, a status and a type.
4. Later the reminder needs steps: check preferences, send, record. Now you write a Workflow that calls the same Activity. The Activity does not change.

## How it works
- The client sends a start request for one Activity with an ID, a task queue and a timeout. Temporal stores it and puts it on the queue (after the delay, if there is one).
- A Worker that has the Activity registered picks it up and runs it. The Retry Policy and timeouts work exactly as inside a Workflow (concept 03).
- There is no Workflow history. The Activity has its own record: ID, status, attempts and result, which you can list with `temporal activity list`.
- `execute` waits for the result. `start` returns a handle immediately.

## Production use cases
- **Notifications**: a welcome message now, a reminder later.
- **Webhook handlers**: a request arrives and one Activity does the work with retries, so the handler can return quickly.
- **Backfills and one-off jobs**: one Activity per record, each with its own retries and status.
- **Calling slow or unreliable APIs** from a service that has no Workflows yet.

## Design decisions
- **Activity or Workflow?** One independent step is an Activity. The moment you need a second step, a wait for a message, or an undo, use a Workflow.
- **ID**: tie it to the business item so duplicates are caught.
- **Timeouts**: Start-to-Close for each attempt, Schedule-to-Close for the whole job including the delay and retries.
- **Idempotency**: an Activity can run more than once, so make the work safe to repeat.

## Failure modes
- **No Worker registered the Activity**: it waits on the queue.
- **Same ID while the first is still running**: the second start does not create a second job. Pick IDs that match the business item.
- **A delay without a Schedule-to-Close**: decide how long the job may stay pending before it is useless.

## What to measure
- Activities waiting on the queue (schedule-to-start latency), failures per type, and how many are pending behind a start delay.

## Use it when
- A job is one step: send a message, call an API, run a script.
- You want retries and visibility without writing orchestration.
- One-off or occasional work: a webhook handler fires an Activity, a backfill runs a fixed task for each ID.

## Don't use it when
- The job has several steps that depend on each other, or needs a Signal, a Query or undo steps. Use a Workflow.
- The job repeats on a calendar. Use a [Schedule](06-schedules.md).
- The delay is part of a longer process ("wait 3 days, then check if they logged in"). Use a Timer inside a Workflow.

## Common mistake
Giving two Standalone Activities the same ID while the first is still running. The ID identifies the job, so pick one that matches the business item, like `welcome-notification-user-123`. For a second job on the same item, add a suffix, like `-reminder`.

## Language note
The Activity type name differs: Python registers `send_notification`, Java registers `SendNotification`. `temporal activity list` shows whichever Worker ran it.

## Learn more
- [Standalone Activities](https://docs.temporal.io/standalone-activity)
- [Python: standalone activities](https://docs.temporal.io/develop/python/standalone-activities)
- [Activities](https://docs.temporal.io/activities)
