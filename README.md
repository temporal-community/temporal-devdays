# temporal-devdays

Learn Temporal by doing: a short explanation, an exercise with a failing check, and a complete solution, in Python and in Java.

## Prerequisites
| Need | Check |
|---|---|
| [Temporal CLI](https://docs.temporal.io/cli) | `temporal --version` |
| Python 3.11+ and [uv](https://docs.astral.sh/uv/) | `python3 --version` and `uv --version` |
| or Java 21 and [Maven](https://maven.apache.org/) | `java -version` and `mvn -v` |

You only need the language you use.

## Setup
```bash
git clone https://github.com/temporal-community/temporal-devdays.git
cd temporal-devdays
temporal server start-dev      # keep running, Web UI at http://localhost:8233
```
Then open exercise 1 in the table below and run its check.

## How to use it
1. Read the concept.
2. Open the topic folder in your language and go into `exercise` and finish the TODOs.
3. Run the check until it passes.
4. Compare with the `solution` folder next to it.

A failing check at the start is expected. It tells you which TODO is next.

## Exercises
| # | Concept | Python | Java |
|---|---|---|---|
| 01 | [Workflow, Activity, Worker](concepts/01-hello.md) | [exercise](python/01_hello/exercise) · [solution](python/01_hello/solution) | [exercise](java/01_hello/exercise) · [solution](java/01_hello/solution) |
| 02 | [Durable execution](concepts/02-durable-execution.md) | [exercise](python/02_durable_execution/exercise) · [solution](python/02_durable_execution/solution) | [exercise](java/02_durable_execution/exercise) · [solution](java/02_durable_execution/solution) |
| 03 | [Retries and timeouts](concepts/03-retries.md) | [exercise](python/03_retries/exercise) · [solution](python/03_retries/solution) | [exercise](java/03_retries/exercise) · [solution](java/03_retries/solution) |
| 04 | [Signals, Queries and Updates](concepts/04-messages.md) | [exercise](python/04_messages/exercise) · [solution](python/04_messages/solution) | [exercise](java/04_messages/exercise) · [solution](java/04_messages/solution) |
| 05 | [Event History and replay](concepts/05-history-replay.md) | [exercise](python/05_history_replay/exercise) · [solution](python/05_history_replay/solution) | [exercise](java/05_history_replay/exercise) · [solution](java/05_history_replay/solution) |
| 06 | [Schedules](concepts/06-schedules.md) | [exercise](python/06_schedules/exercise) · [solution](python/06_schedules/solution) | [exercise](java/06_schedules/exercise) · [solution](java/06_schedules/solution) |
| 07 | [Standalone Activities](concepts/07-standalone-activities.md) | [exercise](python/07_standalone_activities/exercise) · [solution](python/07_standalone_activities/solution) | [exercise](java/07_standalone_activities/exercise) · [solution](java/07_standalone_activities/solution) |
| 08 | [Testing](concepts/08-testing.md) | [exercise](python/08_testing/exercise) · [solution](python/08_testing/solution) | [exercise](java/08_testing/exercise) · [solution](java/08_testing/solution) |
| 09 | [Activity design](concepts/09-activity-design.md) | [exercise](python/09_activity_design/exercise) · [solution](python/09_activity_design/solution) | [exercise](java/09_activity_design/exercise) · [solution](java/09_activity_design/solution) |
| 10 | [Saga across services](concepts/10-saga-across-services.md) | [exercise](python/10_saga_across_services/exercise) · [solution](python/10_saga_across_services/solution) | [exercise](java/10_saga_across_services/exercise) · [solution](java/10_saga_across_services/solution) |
| 11 | [Start or attach](concepts/11-start-or-attach.md) | [exercise](python/11_start_or_attach/exercise) · [solution](python/11_start_or_attach/solution) | [exercise](java/11_start_or_attach/exercise) · [solution](java/11_start_or_attach/solution) |
| 12 | [Human in the loop](concepts/12-human-in-the-loop.md) | [exercise](python/12_human_in_the_loop/exercise) · [solution](python/12_human_in_the_loop/solution) | [exercise](java/12_human_in_the_loop/exercise) · [solution](java/12_human_in_the_loop/solution) |
| 13 | [Fan-out, fan-in and child Workflows](concepts/13-fan-out-fan-in.md) | [exercise](python/13_fan_out_fan_in/exercise) · [solution](python/13_fan_out_fan_in/solution) | [exercise](java/13_fan_out_fan_in/exercise) · [solution](java/13_fan_out_fan_in/solution) |
| 14 | [Entity Workflow and Continue-As-New](concepts/14-entity-continue-as-new.md) | [exercise](python/14_entity_continue_as_new/exercise) · [solution](python/14_entity_continue_as_new/solution) | [exercise](java/14_entity_continue_as_new/exercise) · [solution](java/14_entity_continue_as_new/solution) |
| 15 | [Safe deploys](concepts/15-safe-deploys.md) | [exercise](python/15_safe_deploys/exercise) · [solution](python/15_safe_deploys/solution) | [exercise](java/15_safe_deploys/exercise) · [solution](java/15_safe_deploys/solution) |
| 16 | [An AI agent loop](concepts/16-ai-agent-loop.md) | [exercise](python/16_ai_agent_loop/exercise) · [solution](python/16_ai_agent_loop/solution) | [exercise](java/16_ai_agent_loop/exercise) · [solution](java/16_ai_agent_loop/solution) |
| 17 | [Worker tuning and performance](concepts/17-worker-tuning.md) | [exercise](python/17_worker_tuning/exercise) · [solution](python/17_worker_tuning/solution) | [exercise](java/17_worker_tuning/exercise) · [solution](java/17_worker_tuning/solution) |

## Demos
- [Order management](demos/order-management): four services in Python or Java behind a live web page: retries, a service going down, a Saga, a child Workflow, a Signal and an Update. One command to run it, and a [RUNBOOK](demos/order-management/RUNBOOK.md) to present it.
- [Observability](demos/observability): logs, traces, metrics and a Search Attribute, with or without Docker.

## Troubleshooting
- Port 7233 in use: another dev server is running. Stop it, or use it.
- A Worker from an earlier run is still polling: stop it before starting a new one.
- Blank Web UI: upgrade the CLI (`brew upgrade temporal`).

## Contributing and license
See [CONTRIBUTING.md](CONTRIBUTING.md). MIT, see [LICENSE](LICENSE).
