# Exercise 14: Entity Workflow and Continue-As-New

Keep a long-lived cart Workflow's history small by continuing as new.

[Concept](../../../concepts/14-entity-continue-as-new.md) · Slides: `can-code`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd java/14_entity_continue_as_new/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`CartWorkflowImpl.java`): continue as new with the items so far and `runs + 1`

## The check
After 25 items the cart has all 25 items and has continued as new at least once.

## Stretch
Change `max_per_run` to 5 and count the runs.

Solution: [solution](../solution)
