# Exercise 04: Signals, Queries and Updates

Talk to a running Workflow: approve it, read its status, and change its amount.

[Concept](../../concepts/04-messages.md) · Slides: `demo-messages, signals-define, query-code, update-code`

## Run the check
```bash
temporal server start-dev      # only needed for the live run below
cd java/exercise_04_messages
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`TransferWorkflowImpl.java`): `approve`: set `approved` to `true`
- [ ] TODO 2 (`TransferWorkflowImpl.java`): `getStatus`: return `status`
- [ ] TODO 3 (`TransferWorkflowImpl.java`): `setAmount`: store the amount and return it
- [ ] TODO 4 (`TransferWorkflowImpl.java`): `checkAmount`: reject an amount that is not positive

## The check
The Query returns `waiting`, the Update returns `150`, a negative Update is rejected, and after the Signal the result is `sent 150`.

## Run it live
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter     # terminal 3
```

From the CLI, with a Worker and the starter running: `temporal workflow signal --workflow-id transfer-1 --name approve`, `temporal workflow query --workflow-id transfer-1 --name get_status`, `temporal workflow update execute --workflow-id transfer-1 --name set_amount --input 150`.

## Stretch
Send the Update with `-5` from the CLI and read the error.

Solution: [solution_04_messages](../solution_04_messages)
