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

## Troubleshooting
- Port 7233 in use: another dev server is running. Stop it, or use it.
- A Worker from an earlier run is still polling: stop it before starting a new one.
- Blank Web UI: upgrade the CLI (`brew upgrade temporal`).

## Contributing and license
See [CONTRIBUTING.md](CONTRIBUTING.md). MIT, see [LICENSE](LICENSE).
