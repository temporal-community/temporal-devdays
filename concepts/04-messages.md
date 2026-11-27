# 04: Signals, Queries and Updates

## The problem
A money transfer waits for a manager's approval. While it waits, a clerk wants to see its status and change the amount. How do you talk to a Workflow that is already running, without a database table that both sides poll?

## The idea
There are three ways to send a message to a running Workflow. A **Signal** pushes information in and returns nothing. A **Query** reads the current state and never changes it. An **Update** changes state and returns an answer, and it can reject bad input with a **validator** before anything is recorded.

## Picture
```
Signal  ──▶ Workflow   "approve"             (no answer)
Query   ◀─▶ Workflow   "what is the status?" (read only)
Update  ◀─▶ Workflow   "set amount to 150"   (change + answer)
```

## How it works
- A **Signal** is written to the history as an event and your Workflow handler runs. The sender gets no result and does not know whether the Workflow acted on it.
- A **Query** runs a read-only handler against the Workflow's current state. It is not written to the history, so it does not grow it.
- An **Update** is recorded, runs your handler, and returns a value to the caller. A **validator** runs first. If it rejects the request, the caller gets an error and nothing is written to the history.
- A Workflow can wait for a message with `wait_condition` (Python) or `Workflow.await` (Java), and it costs nothing while waiting.

## Production use cases
- **Approvals**: a refund over a limit waits for a manager (Signal or Update), with a timeout that escalates if nobody answers.
- **Support tools**: a dashboard queries a Workflow for "where is this order" without touching the database.
- **Editing in flight**: change a delivery address before the parcel ships (validated Update), and be told at once if it is too late.
- **Cancel and pause controls** for a long job.

## Design decisions
- **Signal or Update?** Use a Signal when the sender does not need an answer. Use an Update when the sender needs the result or a rejection. Updates give a faster, clearer error path for bad input.
- **Validate early**: reject bad Updates in the validator, so they never enter the history. A validator must only read state: no changes, no Activities, no waiting.
- **Keep Queries cheap and read-only.** They must not start Activities or change state.
- **Waiting with a time limit**: a Workflow that waits for a person should also have a timeout and a plan for when it fires (escalate, cancel, remind).
- **Message volume**: each Signal adds history events. A flood of Signals to one Workflow is a design problem. Batch them or restructure.

## Failure modes
- **Signal sent to a Workflow that has finished**: the send fails. Check the Workflow is still Running if the sender needs to know.
- **Signal sent before the Workflow reaches the handler**: it is still delivered. Handlers should be safe to run at any point.
- **State changed inside a Query**: it is lost on replay and gives wrong answers. Use an Update.
- **Waiting forever**: an unanswered approval holds a Workflow open with no end.

## What to measure
- How many Workflows are waiting on a message, and the age of the oldest.
- Rejected Updates per type, which shows what users get wrong.
- History size of Workflows that receive many Signals.

## Use it when
- A running Workflow needs input from a person or another system (Signal, Update).
- Someone needs to look inside without disturbing it (Query).

## Don't use it when
- All inputs are known when you start the Workflow. Pass them as arguments instead.

## Common mistake
Changing state inside a Query. Queries must only read. Use an Update when you need to change something and get a reply.

Also, waiting for a Signal with no time limit. This exercise does that to stay simple. A real Workflow adds a timeout so an unanswered request does not wait forever.

## Learn more
- [Sending messages](https://docs.temporal.io/sending-messages)
- [Python: message passing](https://docs.temporal.io/develop/python/message-passing)
- [Java: message passing](https://docs.temporal.io/develop/java/message-passing)
- [Workflows](https://docs.temporal.io/workflows)
