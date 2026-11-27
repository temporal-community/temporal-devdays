# Exercise 11: Start or attach

Stop double starts with a business Workflow ID, and add items to a running order with one call.

[Concept](../../../concepts/11-start-or-attach.md) · Slides: `signal-with-start`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd java/11_start_or_attach/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`Orders.java`): `startTransfer`: use `transfer-<id>` as the Workflow ID
- [ ] TODO 2 (`Orders.java`): `addItem`: use `newSignalWithStartRequest` so one call starts or attaches

## The check
A second start of the same transfer is rejected, and two `add_item` calls on one order give one Workflow holding both items.

## Run it live
No live run for this topic: the check is the exercise.

## Stretch
Start the same transfer a third time and read the error you get.

Solution: [solution](../solution)
