# Solution 17: Tune a Worker

Complete code for [exercise](../exercise). Concept: [17-worker-tuning.md](../../../concepts/17-worker-tuning.md).

## What changed
TODO 1 and TODO 2 return the two numbers. The Worker passes them as `max_concurrent_activities` and `max_activities_per_second` (Java: `setMaxConcurrentActivityExecutionSize` and `setMaxWorkerActivitiesPerSecond`). The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
Slots decide how many Activities run together. The rate limit decides how fast new ones start. Together they protect your machine and anything your Activities call.

## Run the tests
```bash
cd java/17_worker_tuning/solution
mvn test
```
No server needed.

## Run it live
Start `temporal server start-dev` in terminal 1, then run these from `java/17_worker_tuning/solution`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
The starter renders 30 items. With 5 starts per second it takes about 5 seconds. Open Task Queues in the Web UI at http://localhost:8233 and watch the `render` queue drain. Then change the numbers in `Tuning.java`, restart the Worker and run the starter again. Compare the time.
