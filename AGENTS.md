# AGENTS.md

Guide for humans and coding agents working in this repo.

## What this repo is
A learn-and-practice resource for Temporal. Each concept has a plain-words explanation in `concepts/`, then an exercise to practise (with a failing check) and a complete solution, in Python and in Java.

## Commands
| Task | Command |
|---|---|
| Install Python deps | `make setup` |
| Run every check (no server needed) | `make check` |
| Run checks plus live smoke tests | `make check-live` (needs `temporal server start-dev`) |
| Dev server and Web UI | `temporal server start-dev` then http://localhost:8233 |
| Python exercise | `cd python/NN_topic/exercise && uv run pytest` |
| Java exercise | `cd java/NN_topic/exercise && mvn test` |

## Repo map
- `concepts/NN-topic.md`: one explanation per topic, shared by both languages.
- `python/` and `java/`: one folder per topic, `NN_topic/`, with `exercise/` and `solution/` inside. Same NN and topic name in both languages.
- `scripts/`: `check.sh`, `verify_repo.py` (structure), `behaviour.py` (tests and live smoke), `live.json` (live smoke table).
- `demos/`: larger demos, added later, one per PR.

## Exercise rules
- Beginner level. Short code that fits on a screen. Minimal comments.
- Every exercise folder is self-contained: no shared package, no imports from another folder.
- An exercise has `TODO N start` / `TODO N end` markers. Unfinished code fails with a message that names the TODO: `NotImplementedError` / `UnsupportedOperationException` in normal code, and a non-retryable `ApplicationError` / `ApplicationFailure` inside a Workflow (a plain exception there would retry forever). Write the solution with `TODO N start` / `TODO N end` markers and generate the exercise with `python3 scripts/make_exercise.py <solution folder>`.
- Every TODO number is listed in the exercise README checklist.
- The solution has the same files, all TODOs done, tests passing.
- A recorded `history.json` contains your host name in every `identity` field. Replace it with `1@devdays` before you commit (`sed -E 's/[0-9]+@[^"]+/1@devdays/'`); the checker rejects host names.
- Never edit a `solution/` folder when you only mean to change the `exercise/` next to it, and the reverse.
- A README is minimal: goal in two lines, run commands, task checklist, what the check expects, one stretch task. The teaching lives in the concept file.
- Concept files use these headings: `## The problem`, `## The idea`, `## Picture`, `## Use it when`, `## Don't use it when`, `## Common mistake`, `## Learn more`. Links go to docs.temporal.io and are checked before they are added.

## Python
- Python 3.11+, `uv`, async code, dataclasses for inputs, pytest.
- Workflow and Activity code in separate files. Import Activities in a Workflow file inside `workflow.unsafe.imports_passed_through()`.

## Java
- Java 21, Maven, `io.temporal:temporal-sdk` pinned in each `pom.xml`, JUnit 5, records for inputs.
- Workflow and Activity are an interface plus an `Impl` class. Entry points are `WorkerApp` and `Starter`: `mvn -q compile exec:java@worker` and `@starter`.
- Fail on purpose with `ApplicationFailure.newNonRetryableFailure(...)`. Sagas use `io.temporal.workflow.Saga` and call `addCompensation` before the step.

## Temporal rules
- Workflows are deterministic: no random, no clock, no I/O. Use Activities, `workflow.now()`, `asyncio.sleep` / `Workflow.sleep`.
- Log in Workflows with `workflow.logger` / `Workflow.getLogger`, never `print`.
- Always set `start_to_close_timeout` on Activities.
- Facts agents get wrong: default Retry Policy is 1s initial interval, 2.0 backoff, 100s max interval, unlimited attempts. History warns at 10,240 events or 10 MB and hard-limits at 51,200 events or 50 MB. Payload limit is 2 MB. Activity type names are `snake_case` in Python and `PascalCase` in Java (`get_distance` vs `GetDistance`).
- Standalone Activities exist in the Python and Java SDKs; `start_delay` is a direct argument of Python `client.start_activity`.

## Git
- Fork, then work on a branch in your fork: `feat/...`, `fix/...`, `docs/...`.
- Conventional Commits. Small, focused PRs to `upstream/main`. Rebase on `upstream/main` before opening. Squash-merge. Never force-push `main`.
- Never commit `target/`, `.venv/`, `.env`, secrets, absolute paths, personal emails or customer names. Commit with a no-reply email, not a personal one.

## Do not
- Do not share code between exercise folders.
- Do not add a link you have not opened.
- Do not claim a check passes without running `make check`.

## Definition of done
`make check` passes, the exercise and solution both have a README, the concept file exists, and the root README table links to the new folders.
