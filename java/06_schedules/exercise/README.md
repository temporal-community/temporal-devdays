# Exercise 06: Schedules

Create a Schedule that starts a Workflow every minute.

[Concept](../../../concepts/06-schedules.md) · Slides: `sched-code, demo-sched`

## Run the check
```bash
cd java/06_schedules/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`Schedules.java`): `buildSchedule`: start `ReportWorkflow` (id `report`, task queue `reports`) every minute, limited to 5 runs
- [ ] TODO 2 (`Schedules.java`): `createSchedule`: create it with `client.createSchedule`

## The check
The Schedule starts `ReportWorkflow` on the `reports` queue with id `report` every minute, stops after 5 runs, and it is created as `nightly-report`.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `java/06_schedules/exercise`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter     # terminal 3
```

Then: `temporal schedule trigger --schedule-id nightly-report`, `temporal schedule list`, and open the Schedules page in the Web UI.

## Stop it
The Schedule runs every minute and stops by itself after 5 runs. To stop it sooner, use any one of these:
```bash
mvn -q compile exec:java@delete
temporal schedule delete --schedule-id nightly-report     # remove it
temporal schedule toggle --schedule-id nightly-report --pause --reason "demo over"   # keep it, stop the ticks
```
Runs already started finish on their own. A paused Schedule still exists, so delete it when you are finished.

## Stretch
Pause the Schedule from the Web UI, then trigger it by hand.

Solution: [solution](../solution)
