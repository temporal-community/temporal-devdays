# 17: Worker tuning and performance

## The problem
Your Workflows work, then traffic grows. Orders wait minutes for an Activity to start, a partner API gets hammered, or a Worker runs out of memory. The code is fine. The Workers are not sized for the load.

## The idea
A Worker is a program with limits: how many tasks it runs at once, how many it asks for, how fast it starts them, and how much it remembers. **Tuning** is setting those limits to match your load and your downstream systems, then watching a few numbers to see if you got it right. Change one setting at a time and measure.

## Picture
```
Temporal task queue ──▶ [ pollers ] ──▶ [ slots: 3 Activities at once ] ──▶ your code
                                         └─ rate limit: 5 starts / second
cache of recent Workflows (avoids replay) lives in the Worker's memory
```

## How it works
- **Slots** cap how many tasks run at once: Activity slots (`max_concurrent_activities`, `setMaxConcurrentActivityExecutionSize`) and Workflow task slots. A full Worker leaves tasks waiting in the queue.
- **Pollers** are the requests a Worker keeps open to fetch tasks. Too few and a Worker with free slots waits idle for work. Newer SDKs can adjust the poller count for you (poller autoscaling).
- **Rate limits**: a per-Worker limit on Activity starts per second (`max_activities_per_second`), and a per-task-queue limit (`max_task_queue_activities_per_second`) applied by the server across all Workers.
- **Sticky cache**: a Worker keeps recent Workflows in memory (`max_cached_workflows`, `setWorkflowCacheSize`). A cached Workflow continues without replay. A small cache means more replay and more load.
- **Resource-based tuning**: instead of fixed slot numbers, a Worker can add and remove slots to hold CPU and memory near a target.
- **Task queue and Worker count**: more Workers on a queue share its tasks. Separate queues isolate different kinds of work.

## Symptom to knob
| What you see | Likely cause | What to try |
|---|---|---|
| Activities wait before starting (high schedule-to-start latency) while Workers are busy | not enough slots or Workers | more Worker processes first, then more slots |
| Activities wait but Workers look idle | not enough pollers | raise the poller count |
| A downstream API returns "too many requests" | Activities start faster than it allows | per-Worker or per-queue rate limit, and a Retry Policy with backoff |
| Worker memory climbs | cache or slots too large for the machine | smaller cache, fewer slots, bigger machine |
| Many Workflow replays, high CPU | cache too small | larger cache |
| Heavy Activity slows light ones on one queue | shared queue | a separate task queue and Worker for the heavy Activity |
| Workflow tasks slow | heavy work or large payloads in Workflow code | move work to Activities, pass references |

## Production use cases
- **Protecting a partner API** that allows 50 calls per second: limit starts so retries and bursts never exceed it.
- **Sharing one machine** between fast API calls and slow rendering: give rendering its own queue and Worker with few slots.
- **Black Friday traffic**: scale Worker processes ahead of time, watch backlog and latency, and scale back.
- **Large fan-outs** (concept 13): slots decide how fast 10,000 Activities drain.

## Design decisions
- **Scale out before tuning up**: add Worker processes before raising slot counts on one machine.
- **Match slots to the work**: CPU-heavy Activities need few slots, waiting on the network allows many.
- **Isolate**: slow, heavy, or rate-limited work gets its own task queue.
- **Always set timeouts and heartbeats**: a heartbeat on a long Activity lets Temporal notice a dead Worker quickly instead of waiting out the full timeout (concept 03).
- **Keep payloads and histories small**: payload limit is 2 MB, and a history warns at 10,240 events. Use references and Continue-As-New (concept 14).
- **Load-test on purpose**: use a load script (exercise 17 has one) before the real traffic does.

## Failure modes
- **Slots too high**: out-of-memory kills, or a downstream system overloaded by your concurrency.
- **Slots too low**: a growing backlog while machines idle.
- **A rate limit too low**: work arrives faster than it drains and the queue grows without limit.
- **Everything on one queue**: one slow Activity starves the rest.
- **Tuning by guess**: a change with no measurement before and after.

## What to measure
- **Schedule-to-start latency** for Activities and Workflow tasks: the main sign that Workers are behind. The SDK exposes it as metrics, for example `temporal_activity_schedule_to_start_latency` (check the exact names in your Prometheus).
- **Slot use**: available versus used slots per Worker.
- **Task failures and retries** by type, and **Activity execution latency** (the observability demo shows `temporal_activity_execution_latency`).
- **Worker CPU and memory**, and the sticky cache hit rate.
- **Backlog** on the task queue (the Web UI shows it under Task Queues).

## Use it when
- Tasks wait before they start, or Workers are overloaded.
- You must protect a downstream system from too much load.

## Don't use it when
- You have no load problem yet. Start with the defaults, add metrics, and tune when the numbers say so.

## Common mistake
Raising slot counts to fix a slow Activity. More slots only help when tasks are waiting for a free slot. If the Activity itself is slow, or the downstream system is the limit, more slots make it worse.

## Learn more
- [Worker performance](https://docs.temporal.io/develop/worker-performance)
- [Workers](https://docs.temporal.io/workers)
- [Worker metrics](https://docs.temporal.io/references/sdk-metrics)
- [Task Queues](https://docs.temporal.io/task-queue)
