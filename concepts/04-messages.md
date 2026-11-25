# 04: Signals, Queries and Updates

## The problem
A money transfer waits for a manager's approval. While it waits, a clerk wants to see its status and change the amount. How do you talk to a Workflow that is already running?

## The idea
There are three ways to send a message to a running Workflow. A **Signal** pushes information in and returns nothing. A **Query** reads the current state and never changes it. An **Update** changes state and returns an answer, and it can reject bad input with a **validator** before anything is recorded.

## Picture
```
Signal  ──▶ Workflow   "approve"             (no answer)
Query   ◀─▶ Workflow   "what is the status?" (read only)
Update  ◀─▶ Workflow   "set amount to 150"   (change + answer)
```

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
