# 12: Human in the loop

## The problem
A large payment needs a manager's approval. The manager may answer in five minutes or in two days, or never. The Workflow must wait without holding a server, and it must not wait forever.

## The idea
The Workflow waits for a **Signal** (the approval) together with a **timeout**. If the Signal arrives, it continues. If the time runs out, the Workflow takes the "nobody answered" path. The wait is a durable Timer, so it costs nothing while it waits and survives restarts.

## Picture
```
wait up to 24h for "approve"
   ├─ approve("alice") arrives ──▶ "approved by alice"
   └─ 24h pass, no answer      ──▶ "timed out"
```

## How it works
- The Workflow waits on a condition (an approval flag set by a Signal) with a timeout. In Python that is `wait_condition` with a timeout, in Java `Workflow.await` with a duration.
- While waiting, the Workflow is not held in memory. The wait is a Timer in Temporal, so it survives restarts and costs almost nothing.
- Whichever comes first wins: the Signal continues the Workflow, the timeout sends it down the "no answer" path.
- An Update can change the request while it waits (a new amount or address), and a Query can show its status to a screen.

## Production use cases
- **Large payments and refunds**: a manager approves within 24 hours, or the payment is cancelled.
- **Content and account review**: a moderator decides, and an unreviewed item is escalated.
- **Onboarding with a human step**: a document check or a call, with reminders along the way.
- **Order approval in the order-management demo**: approve, or change the address first.

## Design decisions
- **What happens at the timeout?** Cancel, escalate, remind, or auto-approve. Pick one on purpose.
- **Reminders**: wait in steps (3 days, then remind, then 4 more) rather than one long wait with silence.
- **Who may approve?** Check the approver in your API, or in an Update validator, before sending the message.
- **Signal or Update?** A Signal for "approve". An Update when the caller needs to be told the request was rejected.
- **Status for the UI**: expose a Query with the current step and who is being waited on.

## Failure modes
- **No timeout**: Workflows stay open forever.
- **Approval arrives after the timeout**: define what happens, usually "too late, ignored".
- **Duplicate approvals**: handle them safely, because a person may click twice.
- **Nobody knows which Workflows are waiting**: add a Search Attribute or a dashboard of open approvals.

## What to measure
- Number of Workflows waiting for a person, and the age of the oldest.
- Time to decision, and the share that ends in a timeout.

## Use it when
- A step needs a person's decision.
- You want a clear outcome when nobody answers.

## Don't use it when
- The decision can be made by code. Then it is just an `if`.

## Common mistake
Waiting for a Signal with no timeout. If nobody answers, the Workflow stays open forever and nobody notices. Always decide what happens when the wait ends.

## Learn more
- [Sending messages](https://docs.temporal.io/sending-messages)
- [Python: message passing](https://docs.temporal.io/develop/python/message-passing)
- [Java: message passing](https://docs.temporal.io/develop/java/message-passing)
- [Python: timers](https://docs.temporal.io/develop/python/timers)
- [Java: timers](https://docs.temporal.io/develop/java/timers)
