# Exercise 08: Testing

Write the tests: one Activity test, a Workflow test where 30 minutes pass instantly, a failure test and a replay test.

[Concept](../../../concepts/08-testing.md) · Slides: `test-unit, test-int, test-replay-code, demo-test`

## Run the check
```bash
cd python/08_testing/exercise
uv run pytest
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`test_activity.py`): test `send_bill` with an `ActivityEnvironment`
- [ ] TODO 2 (`test_workflow.py`): run the order with `run_pizza()`, check the bill and that it took under 20 seconds
- [ ] TODO 3 (`test_workflow.py`): run the order with the fake `far_away` Activity and expect a `WorkflowFailureError`
- [ ] TODO 4 (`test_replay.py`): replay `history.json` against `PizzaWorkflow`

## The check
Your four tests pass. They test code that already works, so a failing check means a test is still a TODO.

## Stretch
Break `PizzaWorkflow` on purpose (swap two lines) and watch your replay test fail.

Solution: [solution](../solution)
