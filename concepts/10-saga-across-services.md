# 10: Saga across services

## The problem
A money transfer touches three services: fraud check, accounts, notifications. If the deposit fails after the withdrawal worked, the money has left one account and arrived nowhere. There is no database transaction that spans services.

## The idea
A **Saga** is a list of undo steps. Before each step that changes something, you note how to undo it. If a later step fails, you run the undo steps in reverse order. The Workflow acts as the **orchestrator**: it calls each service in turn, and each service runs its own Worker on its own Task Queue.

## Picture
```
check_fraud → [note undo] withdraw → deposit ✗
                                      │
                     undo_withdraw ◀──┘   (runs the notes in reverse)
```

## Use it when
- A business action spans several services and each step can be undone.
- You need a clear "money returned" outcome when something fails midway.

## Don't use it when
- A step cannot be undone (an email already sent). Order the steps so those come last.

## Common mistake
Noting the undo after the step. If the step works on the server but the reply is lost, or it fails after it landed, the undo was never noted and never runs. Note the undo first, and make each undo safe to run when there is nothing to undo.

## Learn more
- [Saga pattern](https://docs.temporal.io/design-patterns/saga-pattern)
- [Design patterns](https://docs.temporal.io/design-patterns)
- [Activities](https://docs.temporal.io/activities)
- [Task Queues](https://docs.temporal.io/task-queue)
