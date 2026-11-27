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

## How it works
- The Workflow holds the loop and the conversation so far. Each pass asks the model what to do (an Activity), runs the chosen tool (an Activity), adds the result, and asks again until the model gives an answer or a limit is reached.
- Every model and tool result is recorded in the history. After a crash, the Workflow replays from the history and continues with the next call. Paid calls that finished are not repeated.
- A **Retry Policy** on the model Activity handles rate limits and timeouts, with backoff, in one place.
- A Signal or Update lets a person add input or approve a risky tool, and a Query shows the current state.

## Production use cases
- **Support and research agents**: a loop of search, read, summarise, with every step recorded.
- **Agents that act**: refund an order or change a setting, after a person approves (concept 12).
- **Multi-step document pipelines**: extract, classify, check, write back, with retries on each model call.
- **Long-running assistants** that wait for a user's reply for hours or days.

## Design decisions
- **Everything non-deterministic is an Activity**: model calls, tool calls, the clock, random numbers.
- **Limit the loop**: a maximum number of steps and a timeout, so an agent cannot run for ever or spend without a bound.
- **Retry and timeouts per Activity**: model calls get a longer Start-to-Close and backoff for rate limits. Tools may need their own policies.
- **Tools must be safe to repeat**: use idempotency keys for tools that change things.
- **Keep payloads small**: store long context and files elsewhere and pass references (payload limit is 2 MB).
- **Long conversations**: use Continue-As-New (concept 14) so the history does not grow without limit.
- **Human approval** for tools with side effects.

## Failure modes
- **Model call inside the Workflow**: different answer on replay, so the Workflow breaks.
- **No step limit**: a loop that never ends and keeps costing money.
- **Non-idempotent tools**: a retry sends the email twice.
- **Retrying a final error**: a bad request retries for ever. Mark it non-retryable.
- **Huge context in every call**: slow, expensive and may exceed payload limits.

## What to measure
- Steps per run, model calls and tool calls per type, and their failure and retry rates.
- Latency per model call, and rate-limit errors.
- Cost per run, tracked from the model Activity's results.
- Runs that hit the step limit.

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
