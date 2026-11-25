# 01: Workflow, Activity, Worker

## The problem
You order a pizza app to say hello to a customer. The greeting must be sent even if your server restarts in the middle. Plain code loses its place when the process dies.

## The idea
A **Workflow** is your plain code that Temporal remembers. An **Activity** is one step inside it that talks to the outside world (a database, an email, an API). A **Worker** is a small program you run that does the actual work and asks Temporal "anything for me?" on a **Task Queue**.

## Picture
```
starter ──start──▶ Temporal ◀──polls── Worker
                    (remembers)         ├─ runs the Workflow code
                                        └─ runs the Activities
```

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
