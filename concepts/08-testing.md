# 08: Testing Workflows

## The problem
Your Workflow waits 30 minutes, and then charges a card. You cannot wait 30 minutes in every test, and you do not want a test to charge a real card.

## The idea
Temporal gives you three testing tools. Test an **Activity** on its own with an Activity test environment. Test a **Workflow** with a test environment that has **time skipping**: a 30-minute Timer finishes in milliseconds. Replace a real Activity with a fake one to test the unhappy path. Finally, **replay** a recorded history to check that new code still matches old runs.

## Picture
```
Activity test   : call send_bill directly
Workflow test   : fake clock, 30 minutes pass instantly, fake Activities
Replay test     : recorded history  +  your code  →  still matches?
```

## How it works
- **Activity test**: call the Activity function inside an Activity test environment (`ActivityEnvironment` in Python, `TestActivityEnvironment` in Java). You get a real Activity context without a server.
- **Workflow test**: start a test environment, register your Workflow and Activities on a Worker in it, and run the Workflow with fresh IDs for each test. `WorkflowEnvironment.start_local()` (Python) runs a real local server and is the default choice. `start_time_skipping()` (Python) or `TestWorkflowEnvironment` (Java) runs an in-process test server whose clock jumps ahead while the Workflow waits on a Timer. Use it only when the Workflow has long Timers, because it cannot be shared between tests and does not behave like the full server.
- **Fakes**: register your own Activity with the same name as the real one, returning what the test needs or raising an error.
- **Replay test**: load an exported history and run your Workflow code against it. A mismatch fails the test.

## Production use cases
- **Business rules**: "an order over the limit waits for approval; after the timeout it is cancelled". Test the timeout path in milliseconds.
- **Failure handling**: make the payment Activity fail with a final error and check the Saga undoes the earlier steps.
- **Deploy gates**: a CI step that replays a folder of recorded histories on every pull request.

## Design decisions
- **Test the Workflow with fake Activities, and each Activity on its own.** The Workflow test checks the decisions, the Activity test checks the work.
- **Cover each branch**: happy path, retry then success, final failure, timeout, each Signal and Update.
- **Keep a small set of real histories** in the repo, one per Workflow type and per important branch, for replay.
- **Time skipping has limits**: it only skips while the Workflow is waiting. An Activity that takes real time still takes real time.
- **An integration test is separate**: run one against a dev server for the whole path, and keep it small.

## Failure modes
- **Real calls in tests**: slow, flaky, and a risk to real accounts. Use fakes.
- **A fake with a different name from the real Activity**: the Workflow waits for an Activity nobody runs, and the test hangs.
- **Only testing the happy path**: the bugs live in retries, timeouts and undo.
- **A stale history file**: replay passes against old code paths only. Refresh histories when the Workflow changes on purpose.

## What to measure
- Test run time. Workflow tests with time skipping should take milliseconds to seconds.
- Replay tests in CI: count and pass rate.
- Which Workflow branches have a test, from your own coverage report.

## Use it when
- You want fast, repeatable tests for Workflow logic.
- You change Workflow code and want to protect running Workflows.

## Don't use it when
- You need to prove a real external service works. That is an integration test against that service.

## Common mistake
Putting real calls (payments, emails) in Activities and then running them in tests. Register fake Activities with the same name in the test instead.

## Learn more
- [Python: testing suite](https://docs.temporal.io/develop/python/testing-suite)
- [Java: testing suite](https://docs.temporal.io/develop/java/testing-suite)
- [Event History](https://docs.temporal.io/encyclopedia/event-history)
