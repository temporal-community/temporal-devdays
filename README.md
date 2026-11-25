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
2. Open `exercise_NN` in your language and finish the TODOs.
3. Run the check until it passes.
4. Compare with `solution_NN`.

A failing check at the start is expected. It tells you which TODO is next.

## Exercises
| # | Concept | Python | Java |
|---|---|---|---|
| 01 | [Workflow, Activity, Worker](concepts/01-hello.md) | [exercise](python/exercise_01_hello) · [solution](python/solution_01_hello) | [exercise](java/exercise_01_hello) · [solution](java/solution_01_hello) |
| 02 | [Durable execution](concepts/02-durable-execution.md) | [exercise](python/exercise_02_durable_execution) · [solution](python/solution_02_durable_execution) | [exercise](java/exercise_02_durable_execution) · [solution](java/solution_02_durable_execution) |
| 03 | [Retries and timeouts](concepts/03-retries.md) | [exercise](python/exercise_03_retries) · [solution](python/solution_03_retries) | [exercise](java/exercise_03_retries) · [solution](java/solution_03_retries) |
| 04 | [Signals, Queries and Updates](concepts/04-messages.md) | [exercise](python/exercise_04_messages) · [solution](python/solution_04_messages) | [exercise](java/exercise_04_messages) · [solution](java/solution_04_messages) |

## Troubleshooting
- Port 7233 in use: another dev server is running. Stop it, or use it.
- A Worker from an earlier run is still polling: stop it before starting a new one.
- Blank Web UI: upgrade the CLI (`brew upgrade temporal`).

## Contributing and license
See [CONTRIBUTING.md](CONTRIBUTING.md). MIT, see [LICENSE](LICENSE).
