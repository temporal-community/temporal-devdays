# Exercise 03: Retries and timeouts

Set a Retry Policy for a flaky Activity and stop retrying an error that can never succeed.

[Concept](../../../concepts/03-retries.md) · Slides: `demo-retries, retry-code, nonretry-code`

## Run the check
```bash
temporal server start-dev      # only needed for the live run below
cd java/03_retries/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`PaymentWorkflowImpl.java`): build the Activity stub with `RetryOptions`: initial interval 10 ms, maximum 5 attempts
- [ ] TODO 2 (`ChargeActivitiesImpl.java`): throw a non-retryable `ApplicationFailure` when the card is `bad`

## The check
The `flaky` card is charged on attempt 3 with a policy of at most 5 attempts, and the `bad` card is tried only once.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `java/03_retries/exercise`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter     # terminal 3
```

Try it live with a Worker running: `starter` with no argument charges the flaky card, and `starter bad` fails at once.

## Stretch
Set the maximum to 2 attempts and watch the flaky card fail.

Solution: [solution](../solution)
