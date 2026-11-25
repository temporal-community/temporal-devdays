# 07: Standalone Activities

## The problem
You only want to send one welcome message, with retries if it fails. Writing a whole Workflow around a single step feels like too much.

## The idea
A **Standalone Activity** runs one Activity directly from your client, with no Workflow around it. You still get retries, timeouts and a result you can wait for, and you can start it with a **start delay** to run it later. The Activity code is the same, so if the job grows you can later call it from a Workflow with no changes.

## Picture
```
client ──execute_activity──▶ Temporal ──▶ Worker runs send_notification ──▶ result
        (no Workflow in between)
```

## Use it when
- A job is one step: send a message, call an API, run a script.
- You want retries and visibility without writing orchestration.

## Don't use it when
- The job has several steps that depend on each other. Use a Workflow.

## Common mistake
Giving two Standalone Activities the same ID while the first is still running. The ID identifies the job, so pick one that matches the business item, like `welcome-notification-user-123`.

## Learn more
- [Standalone Activities](https://docs.temporal.io/standalone-activity)
- [Python: standalone activities](https://docs.temporal.io/develop/python/standalone-activities)
- [Activities](https://docs.temporal.io/activities)
