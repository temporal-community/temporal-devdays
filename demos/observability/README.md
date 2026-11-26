# Demo: observability

One small pizza Workflow, three ways to see what it did: logs, traces and metrics, plus a Search Attribute you can query. Temporal has OpenTelemetry support built in, so this is a few lines of setup, not new code in your Workflow.

| You want to see | How it is wired | Where to look |
|---|---|---|
| Logs | `workflow.logger` and `activity.logger`, replay-safe, with the workflow id attached | Worker terminal |
| Traces | the `TracingInterceptor` on the client, one trace from the starter through the Workflow and every Activity | Worker terminal, or Jaeger |
| Metrics | the SDK's Prometheus endpoint on port 9464 | `curl localhost:9464/metrics`, or Prometheus |
| Where is my order? | `upsert_search_attributes` sets a `Step` attribute | `temporal workflow list --query 'Step = "baking"'` |

### What is in the demo

| Piece | What it is | Where |
|---|---|---|
| `PizzaWorkflow` | the plan for one order. It sets the `Step` attribute as it goes | `workflows.py` |
| `take_order`, `bake`, `deliver` | the Activities: the steps that do the work (simulated) | `activities.py` |
| Worker | runs the Workflow and Activities on the `pizza` task queue, and serves metrics on port 9464 | `worker.py` |
| Starter | starts one order and prints the result | `starter.py` |
| Telemetry setup | turns on tracing, metrics and logging for the Worker and the starter | `telemetry.py` |

### One order, step by step

| # | Step | `Step` attribute | Kind | What you see |
|---|---|---|---|---|
| 1 | order started | `taking order` | Workflow starts | log line, `StartWorkflow` and `RunWorkflow` spans |
| 2 | take the order | `taking order` | Activity | `RunActivity:take_order` span |
| 3 | wait 2 seconds (a Timer) | `preparing` | Workflow waits | the gap in the Jaeger trace |
| 4 | bake | `baking` | Activity | `RunActivity:bake` span |
| 5 | deliver | `delivering` | Activity | `RunActivity:deliver` span |
| 6 | done | `done` | Workflow returns | `CompleteWorkflow` span, log line |

Code to read: `telemetry.py` (the whole setup) and `workflows.py`.

## Run it, no Docker

You need the [Temporal CLI](https://docs.temporal.io/cli), Python 3.11+ and [uv](https://docs.astral.sh/uv/).

```bash
temporal server start-dev --search-attribute Step=Keyword     # terminal 1
cd demos/observability/python
uv run python worker.py                                       # terminal 2
uv run python starter.py                                      # terminal 3
```

Already running a dev server? Add the attribute once: `temporal operator search-attribute create --name Step --type Keyword`.

Watch terminal 2: log lines and one `span ...` line per step, all with the same `trace=` id as the starter. While the order is in its 2 second Timer, run:

```bash
curl -s localhost:9464/metrics | grep temporal_activity_execution_latency_count
temporal workflow list --query 'Step = "preparing"'
```

## Run it with Jaeger and Prometheus (needs Docker)

```bash
cd demos/observability
docker compose up -d
cd python
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
uv run python worker.py                                       # terminal 2, same variable in terminal 3
uv run python starter.py                                      # terminal 3
```

- Jaeger, http://localhost:16686: pick `pizza-worker`, open the trace. The starter and the Worker show as one trace.
- Prometheus, http://localhost:9090: try `temporal_activity_execution_latency_count`.
- Stop with `docker compose down`.

On Linux, replace `host.docker.internal` in `prometheus.yml` with your host address if the target shows as down.

## Test

`uv run pytest -q` starts a local dev server for you and checks the spans, that all spans share one trace, and that the Workflow logs once.

## Not covered

Grafana dashboards, Temporal Cloud metrics and Temporal server metrics. See the [observability docs](https://docs.temporal.io/develop/python/observability) for the full picture.
