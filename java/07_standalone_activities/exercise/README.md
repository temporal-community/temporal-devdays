# Exercise 07: Standalone Activities

Run one Activity with no Workflow around it, now and later.

[Concept](../../../concepts/07-standalone-activities.md) · Slides: `sa-define, sa-run, sa-delay, demo-sa`

## Run the check
```bash
cd java/07_standalone_activities/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`Notifications.java`): `runNow`: run `sendNotification` with `client.execute` and return the result
- [ ] TODO 2 (`Notifications.java`): `runLater`: start it with `client.start` and a start delay of 6 hours

## The check
`run_now` / `runNow` runs the Activity with the right id, queue and timeout, and `run_later` / `runLater` adds a 6 hour start delay.

## Run it live
Do this after the check passes. Start `temporal server start-dev` in terminal 1, then run these from `java/07_standalone_activities/exercise`:
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter     # terminal 3
```

Then: `temporal activity list` and the Standalone Activities view in the Web UI.

## Stretch
Change the message, run it again with a new id, and compare both in `temporal activity list`.

Solution: [solution](../solution)
