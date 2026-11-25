# Exercise 10: Saga across services

Make a transfer across three services undo its withdrawal when a later step fails.

[Concept](../../../concepts/10-saga-across-services.md) · Slides: `saga-code, tr-1`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd java/10_saga_across_services/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`TransferWorkflowImpl.java`): call `saga.addCompensation` BEFORE `accounts.withdraw`
- [ ] TODO 2 (`TransferWorkflowImpl.java`): call `saga.compensate()` in the `catch`

## The check
A failed deposit undoes the withdrawal, a fraud failure has nothing to undo, and a withdrawal that lands and then fails is still undone.

## Run it live
Run the four services, each in its own terminal, then start a transfer:
```bash
mvn -q compile exec:java@worker
mvn -q exec:java@worker -Dworker.main=devdays.AccountsWorker
mvn -q exec:java@worker -Dworker.main=devdays.FraudWorker
mvn -q exec:java@worker -Dworker.main=devdays.NotifyWorker
mvn -q exec:java@starter                        # succeeds
mvn -q exec:java@starter -Dexec.args="deposit"  # fails at deposit, the withdrawal is undone
```

## Stretch
Add a second undo for `deposit` and check the undos run newest first.

Solution: [solution](../solution)
