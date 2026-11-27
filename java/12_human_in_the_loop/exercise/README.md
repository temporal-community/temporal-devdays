# Exercise 12: Human in the loop

Wait for a person's approval, but not forever.

[Concept](../../../concepts/12-human-in-the-loop.md) · Slides: `await-code, signals-define`

## Run the check
```bash
cd java/12_human_in_the_loop/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`ApprovalWorkflowImpl.java`): wait for `approver` with `Workflow.await` and a 24 hour timeout, return `timed out` on timeout
- [ ] TODO 2 (`ApprovalWorkflowImpl.java`): `approve`: remember who approved

## The check
After `approve("alice")` the result is `approved by alice`. With no answer the result is `timed out`.

## Run it live
No live run for this topic: the check is the exercise.

## Stretch
Change the timeout to 1 hour and see that the test still finishes at once.

Solution: [solution](../solution)
