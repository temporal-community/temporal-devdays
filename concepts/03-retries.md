# 03: Retries and timeouts

## The problem
Charging a card fails now and then because the payment service has a bad second. Retrying fixes that. But if the card is simply invalid, retrying ten times only wastes time.

## The idea
Temporal retries failed Activities for you, following a **Retry Policy**: how long to wait, how fast the wait grows, and how many attempts. By default it retries forever with growing waits. Some errors are **non-retryable**: you mark them so Temporal stops at once.

## Picture
```
attempt 1 ✗ ──wait──▶ attempt 2 ✗ ──wait──▶ attempt 3 ✓
invalid card ✗ ───────────────────────────▶ stop (non-retryable)
```

## Use it when
- A step can fail for temporary reasons: network, a busy service.
- You can tell a temporary error from a final one.

## Don't use it when
- The error is final (invalid input, card declined). Mark it non-retryable instead.

## Common mistake
Leaving the default policy on a step that can never succeed. It retries forever. Set a maximum or mark the error non-retryable.

## Learn more
- [Retry Policies](https://docs.temporal.io/encyclopedia/retry-policies)
- [Python: failure detection](https://docs.temporal.io/develop/python/failure-detection)
- [Java: failure detection](https://docs.temporal.io/develop/java/failure-detection)
- [Activities](https://docs.temporal.io/activities)
