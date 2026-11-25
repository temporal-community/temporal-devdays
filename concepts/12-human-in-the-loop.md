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
