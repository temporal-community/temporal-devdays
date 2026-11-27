# Exercise 03: Retries and timeouts

Set a Retry Policy for a flaky Activity and stop retrying an error that can never succeed.

[Concept](../../../concepts/03-retries.md) · Slides: `demo-retries, retry-code, nonretry-code`

## Run the check
```bash
temporal server start-dev      # only needed for the live run below
cd python/03_retries/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`workflows.py`): call `charge` with a `RetryPolicy`: `initial_interval` of 10 ms and `maximum_attempts` of 5
- [ ] TODO 2 (`activities.py`): raise a non-retryable `ApplicationError` when the card is `bad`

## The check
The `flaky` card is charged on attempt 3 with a policy of at most 5 attempts, and the `bad` card is tried only once.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `python/03_retries/exercise`:
```bash
uv run python worker.py      # terminal 2
uv run python starter.py     # terminal 3
```

Try it live with a Worker running: `starter` with no argument charges the flaky card, and `starter bad` fails at once.

## Stretch
Set the maximum to 2 attempts and watch the flaky card fail.

Solution: [solution](../solution)
