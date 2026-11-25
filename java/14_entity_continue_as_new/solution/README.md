# Solution 14: Entity Workflow and Continue-As-New

Complete code for [the exercise](../exercise). Concept: [14-entity-continue-as-new.md](../../../concepts/14-entity-continue-as-new.md).

## What changed
TODO 1 ends the run and starts a new one with the state carried forward. The markers `TODO N start` and `TODO N end` show the lines you wrote; lines that start with `#?` or `//?` show the broken version the exercise started with.

## Why it works
Each run records only its own messages. The state travels in the argument, so the cart loses nothing.

## Run
```bash
cd java/14_entity_continue_as_new/solution
mvn test
```
