# Solution 08: Testing

Complete code for [the exercise](../exercise). Concept: [08-testing.md](../../../concepts/08-testing.md).

## What changed
TODO 1 to 4 are the four tests. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
Time skipping moves the test clock forward, fake Activities stand in for the real ones, and replay checks the code against a recorded run.

## Run
```bash
cd python/08_testing/solution
uv run pytest
```
