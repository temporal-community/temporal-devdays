# 01: Workflow, Activity, Worker

## The problem
You build an onboarding flow: create an account, send a welcome email, provision a workspace. Each step can fail, and the server running it can restart between steps. Plain code loses its place when the process dies, so a customer ends up with an account and no workspace, and nobody knows.

## The idea
A **Workflow** is your plain code that Temporal remembers. An **Activity** is one step inside it that talks to the outside world (a database, an email, an API). A **Worker** is a small program you run that does the actual work and asks Temporal "anything for me?" on a **Task Queue**.

## Picture
```
starter ──start──▶ Temporal ◀──polls── Worker
                    (remembers)         ├─ runs the Workflow code
                                        └─ runs the Activities
```

## How it works
1. Your client asks Temporal to start a Workflow. Temporal records a `WorkflowExecutionStarted` event and puts a task on the Task Queue.
2. A Worker polls the queue, picks up the task, and runs your Workflow code until it needs an Activity.
3. The Workflow does not run the Activity. It asks Temporal to schedule it. Temporal records the request and puts an Activity task on the queue.
4. A Worker (the same one or another) runs the Activity and reports the result. Temporal records it in the **Event History**, an append-only log of everything that happened.
5. The Workflow continues with the result, and the cycle repeats until it returns.

Temporal does not run your code. Your Workers do. Temporal stores the history and hands out tasks. That is why you can scale, restart or move Workers freely.

## Production use cases
- **Customer onboarding**: create account, send email, provision resources, notify sales. One Workflow per customer, so support can look up exactly where one customer is stuck.
- **Order fulfilment**: reserve stock, charge, ship. Each step is an Activity that can be retried on its own.
- **Data pipelines and backfills**: a Workflow walks a list of jobs, and each job is an Activity.

In all three the Workflow ID is a business ID (`onboarding-customer-42`). That makes the Web UI a search box for "what happened to this customer".

## Design decisions
- **What is an Activity?** Anything that touches the outside world or can fail: network calls, databases, files, random numbers, the clock. Keep the Workflow to decisions: `if`, loops, ordering.
- **One Activity per step or fewer, bigger ones?** Each Activity adds events to the history. Do not split a step into ten tiny Activities, and do not merge two services into one Activity (exercise 09 covers this).
- **Workflow ID**: use an ID that matches a business item, so duplicates are blocked and lookups are easy.
- **Task Queue name**: a plain name per service or purpose, the same string in the Worker and the client.

## Failure modes
- **No Worker is running**: the Workflow starts and then waits. The Web UI shows "No workers polling" for that queue. Nothing is lost.
- **Different queue name in the client and the Worker**: same symptom. Check for typos first.
- **Activity not registered on the Worker**: the Activity task fails until you register it.
- **I/O inside the Workflow**: it may work in a test and break on replay (see concept 05).

## What to measure
Later, in production, you will watch Activity and Workflow failure counts, and schedule-to-start latency (how long a task waits for a Worker). Concept 17 covers these. For now, learn to read one run in the Web UI.

## Use it when
- A job has several steps and must finish even if something crashes.
- A step calls something that can fail or be slow.

## Don't use it when
- You only need one quick function call with no recovery.

## Common mistake
Putting the outside-world call (a database or email) straight into the Workflow. Put it in an Activity. The Workflow must stay predictable.

## Learn more
- [Workflows](https://docs.temporal.io/workflows)
- [Activities](https://docs.temporal.io/activities)
- [Workers](https://docs.temporal.io/workers)
- [Task Queues](https://docs.temporal.io/task-queue)
- [Python: core application](https://docs.temporal.io/develop/python/core-application)
- [Java: core application](https://docs.temporal.io/develop/java/core-application)
