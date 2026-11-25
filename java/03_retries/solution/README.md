# Solution 03: Retries and timeouts

Complete code for [exercise](../exercise). Concept: [03-retries.md](../../../concepts/03-retries.md).

## What changed
TODO 1 sets the Retry Policy. TODO 2 marks the invalid card as non-retryable. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The policy caps the retries. A non-retryable error tells Temporal to stop now, so a final error does not retry.

## Run
```bash
cd java/03_retries/solution
mvn test
```
