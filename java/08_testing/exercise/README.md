# Exercise 08: Testing

Write the tests: one Activity test, a Workflow test where 30 minutes pass instantly, a failure test and a replay test.

[Concept](../../../concepts/08-testing.md) · Slides: `test-unit, test-int, test-replay-code, demo-test`

## Run the check
```bash
cd java/08_testing/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`PizzaActivitiesTest.java`): test `sendBill` with a `TestActivityEnvironment`
- [ ] TODO 2 (`PizzaWorkflowTest.java`): run the order, check the bill and that it took under 20 seconds
- [ ] TODO 3 (`PizzaWorkflowTest.java`): run the order with an Activity that returns a distance of 40 and expect a `WorkflowFailedException`
- [ ] TODO 4 (`PizzaReplayTest.java`): replay `history.json` against `PizzaWorkflowImpl`

## The check
Your four tests pass. They test code that already works, so a failing check means a test is still a TODO.

## Run it live
No live run for this topic: the check is the exercise.

## Stretch
Break `PizzaWorkflow` on purpose (swap two lines) and watch your replay test fail.

Solution: [solution](../solution)
