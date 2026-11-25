# 16: An AI agent loop

## The problem
An AI agent asks a model what to do, runs a tool, asks again, and repeats. Model calls are slow, cost money, fail with rate limits, and give a different answer every time. A program that crashes in the middle must not start over and pay again.

## The idea
Put every call to the model and every tool in an **Activity**. The Workflow holds the loop and the conversation, and stays predictable. Because Activity results are recorded, a crash does not repeat finished calls, and a **Retry Policy** on the model Activity handles rate limits in one place. This exercise uses a fake model so you need no API key.

## Picture
```
Workflow loop:  llm_decide ──▶ run_tool ──▶ llm_decide ──▶ answer
                (Activity)     (Activity)    (Activity)
history keeps each result, a crash resumes at the next call
```

## Use it when
- Your code calls a model, an API or a tool in a loop.
- You want retries, limits and a record of every step.

## Don't use it when
- You only call a model once and can simply try again from the start.

## Common mistake
Calling the model directly inside the Workflow. The answer changes on replay, so the Workflow breaks. The model call is the textbook case for an Activity.

## Learn more
- [Temporal AI cookbook](https://docs.temporal.io/ai-cookbook)
- [Activities](https://docs.temporal.io/activities)
- [Retry Policies](https://docs.temporal.io/encyclopedia/retry-policies)
- [Workflow definition and determinism](https://docs.temporal.io/workflow-definition)
