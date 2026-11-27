# 09: Activity design

## The problem
A Workflow that works can still be built badly. Two common habits cause trouble: putting simple decisions into Activities, and putting several outside calls into one Activity.

## The idea
Use an **Activity** only for things that touch the outside world and can fail: a database, an email, an API. Plain decisions (an `if` on the data you already have) belong in the Workflow, because an Activity costs time, history and a worker round trip. Give each outside call its own Activity, so a retry repeats only the step that failed.

## Picture
```
Bad :  create_user = [ save to database + send email ]   retry → saved twice
Good:  add_user ──▶ send_welcome                          retry → only the email
```

## How it works
- Every Activity call adds events to the history (scheduled, started, completed) and a round trip to a Worker. A plain `if` in the Workflow adds none.
- Each Activity has its own Retry Policy and timeouts. A retry repeats the whole Activity, so everything inside it runs again.
- If one Activity makes two outside calls and the second fails, the retry repeats the first call too. That is the "saved twice" bug in the picture.

## Production use cases
- **Claims and approvals**: the decision "over the limit? needs review" is an `if` on the claim data. Only the database write and the notification are Activities.
- **User onboarding**: `add_user` and `send_welcome` are separate Activities, so a failing email never duplicates the account.
- **Payments**: `reserve_funds` and `capture_funds` are separate, each with its own idempotency key.

## Design decisions
- **The test for an Activity**: does it touch the outside world, or can it fail for reasons outside your code? If not, keep it in the Workflow.
- **One outside call per Activity** is the default. Group two calls only when they must succeed or fail together and are safe to repeat.
- **Granularity**: not so small that history bloats, not so big that a retry repeats too much.
- **Idempotency keys**: pass the same key on every attempt so the other system can ignore repeats.
- **Inputs and results are small**: pass IDs, not large blobs (the payload limit is 2 MB).

## Failure modes
- **Too many tiny Activities**: long histories and slow Workflows.
- **One Activity doing two things**: duplicates after a retry.
- **Activities that call each other or hold state**: hard to retry and test.
- **Activity that reads the clock or random numbers to decide**: results differ across retries.

## What to measure
- Events per Workflow run, to spot over-splitting.
- Activity retries per type, to find the Activities that repeat work.
- Activity duration against its Start-to-Close timeout.

## Use it when
- A step calls something outside your code (Activity).
- Two outside calls could fail on their own (two Activities).

## Don't use it when
- The step is pure logic on data you already hold. Keep it in the Workflow.

## Common mistake
Wrapping every function in an Activity "to be safe". It adds cost and hides simple logic. The other direction is just as common: one Activity that writes to a database and then sends an email, so a failed email retry writes the record twice.

## Learn more
- [Activities](https://docs.temporal.io/activities)
- [Activity definition](https://docs.temporal.io/activity-definition)
- [Retry Policies](https://docs.temporal.io/encyclopedia/retry-policies)
- [Idempotency and durable execution](https://temporal.io/blog/idempotency-and-durable-execution)
