# Solution 06: Schedules

Complete code for [the exercise](../exercise). Concept: [06-schedules.md](../../../concepts/06-schedules.md).

## What changed
TODO 1 describes what to run and how often. TODO 2 sends it to Temporal. The markers `TODO N start` and `TODO N end` show the lines you wrote.

## Why it works
The Schedule lives in Temporal. Each tick starts a normal Workflow run, so you can read it in the Web UI like any other.

## Run the tests
```bash
cd java/06_schedules/solution
mvn test
```
No server needed.

## Run it live
Start the dev server first: `temporal server start-dev` (Web UI at http://localhost:8233). Then, from `java/06_schedules/solution`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter      # terminal 3
```
You should see `Created nightly-report`.

## Stop it
The Schedule runs every minute and stops by itself after 5 runs. To stop it sooner, use any one of these:
```bash
mvn -q compile exec:java@delete
temporal schedule delete --schedule-id nightly-report     # remove it
temporal schedule toggle --schedule-id nightly-report --pause --reason "demo over"   # keep it, stop the ticks
```
Runs already started finish on their own. A paused Schedule still exists, so delete it when you are finished.

