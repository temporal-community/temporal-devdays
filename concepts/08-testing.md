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
