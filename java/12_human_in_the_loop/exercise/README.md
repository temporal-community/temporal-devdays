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
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `java/12_human_in_the_loop/exercise`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
The starter shows the approval Workflow waits, then a Signal approves it. You should see `approved by alice`.

## Stretch
Change the timeout to 1 hour and see that the test still finishes at once.

Solution: [solution](../solution)
