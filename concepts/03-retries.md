# 03: Retries and timeouts

## The problem
Charging a card fails now and then because the payment service has a bad second. Retrying fixes that. But if the card is simply invalid, retrying ten times only wastes time. And if the payment service hangs, you need to give up on one attempt without waiting forever.

## The idea
Temporal retries failed Activities for you, following a **Retry Policy**: how long to wait, how fast the wait grows, and how many attempts. By default it retries forever with growing waits. Some errors are **non-retryable**: you mark them so Temporal stops at once. **Timeouts** decide when an attempt, or the whole job, counts as failed.

## Picture
```
attempt 1 ✗ ──wait──▶ attempt 2 ✗ ──wait──▶ attempt 3 ✓
invalid card ✗ ───────────────────────────▶ stop (non-retryable)
```

## How it works
- The default Retry Policy: first wait 1 second, wait doubles each time (backoff 2.0), capped at 100 seconds, unlimited attempts. You can set a maximum number of attempts and a cap on the wait.
- **Start-to-Close** is the time limit for one attempt. It is the timeout you should always set. **Schedule-to-Close** is the limit for the whole job, retries included. **Schedule-to-Start** is how long a task may wait for a Worker. **Heartbeat** timeout is for long Activities that report progress. Heartbeats are also how a cancellation request reaches a running Activity, so an Activity that never heartbeats cannot be cancelled.
- Retries happen inside Temporal, with no code in your Activity. The Workflow does not see the failed attempts, only the final result. The Web UI shows the attempt number and the last error.
- An error you mark non-retryable (in Python `ApplicationError(..., non_retryable=True)`, in Java `ApplicationFailure.newNonRetryableFailure`) stops the retries and reaches the Workflow.

## Production use cases
- **Payments**: retry network errors and 5xx responses, stop at once on "card declined" or "invalid account".
- **Calling a flaky partner API**: retry with backoff for hours, cap the wait, and alert when the job is old.
- **Sending email or SMS**: retry on provider outages, give up after a deadline (Schedule-to-Close) and fall back to another channel.

## Design decisions
- **Set Start-to-Close on every Activity**, sized to the slowest normal run plus margin. A different value per Activity, not one number everywhere.
- **Retry forever or cap it?** Forever is fine when the step must eventually happen and you will be alerted. Cap it (attempts or Schedule-to-Close) when a late result is useless.
- **Which errors are final?** Decide per Activity. Bad input and business rejections are final. Timeouts, 5xx and connection errors are temporary.
- **Idempotency**: an Activity can run more than once, for example if the Worker dies after the call but before reporting. Use an idempotency key with the other system so a retry does not charge twice. The Workflow ID or a business ID (an order number) makes a good key.
- **Do not retry at the Workflow level** to cover Activity failures. Let the Activity's Retry Policy do it.

## Failure modes
- **No timeout set**: an Activity can hang with no way to notice.
- **Retrying something that can never succeed**: an invalid card retries forever with the default policy. The Workflow looks "stuck" while the Activity retries.
- **A tight retry loop against a struggling service**: keep the backoff. A very short wait with unlimited attempts adds load when the service is already down.
- **Non-idempotent Activity**: duplicate side effects after a retry.

## What to measure
- Activity failures and retries per Activity type. A flat line with spikes is normal. A steady climb means a real problem.
- Schedule-to-Start latency (are Workers keeping up?) and Activity execution time against your Start-to-Close.
- The age of the oldest running Workflow in a retrying step.

## Use it when
- A step can fail for temporary reasons: network, a busy service.
- You can tell a temporary error from a final one.

## Don't use it when
- The error is final (invalid input, card declined). Mark it non-retryable instead.

## Common mistake
Leaving the default policy on a step that can never succeed. It retries forever. Set a maximum or mark the error non-retryable.

## Learn more
- [Retry Policies](https://docs.temporal.io/encyclopedia/retry-policies)
- [Activity timeouts](https://docs.temporal.io/encyclopedia/detecting-activity-failures)
- [Python: failure detection](https://docs.temporal.io/develop/python/failure-detection)
- [Java: failure detection](https://docs.temporal.io/develop/java/failure-detection)
- [Activities](https://docs.temporal.io/activities)
