# Demo: order management

A small web app that places an order and shows what Temporal does while it runs. It combines ideas from exercises [03](../../concepts/03-retries.md), [04](../../concepts/04-messages.md), [10](../../concepts/10-saga-across-services.md), [12](../../concepts/12-human-in-the-loop.md) and [13](../../concepts/13-fan-out-fan-in.md).

Inspired by [temporal-order-management-demo](https://github.com/temporal-sa/temporal-order-management-demo). This is a new, smaller implementation, not a copy.

## Run it

You need the [Temporal CLI](https://docs.temporal.io/cli), Python 3.11+ and [uv](https://docs.astral.sh/uv/).

```bash
temporal server start-dev              # terminal 1, Web UI at http://localhost:8233
cd demos/order-management/python
uv run python worker.py                # terminal 2
uv run python app.py                   # terminal 3, open http://localhost:5050
```

Test without a server: `uv run pytest -q`

## Scenarios

| Pick | What happens | Where to look in the Web UI |
|---|---|---|
| Happy path | stock, charge, then a child Workflow ships the three items in parallel | `order-<id>` and its child `ship-<id>` |
| Flaky card | the charge fails twice, the Retry Policy makes attempt 3 pass | the `charge` Activity shows 3 attempts |
| Bad card | non-retryable error, the reserved stock is released (Saga), order cancelled | `release_stock` runs after `charge` fails |
| Needs approval | the Workflow waits for a Signal, an Update can change the address first | click Approve, or Change address (a short address is rejected) |

## Files

- `workflows.py`: `OrderWorkflow` and `ShipmentWorkflow`
- `activities.py`: the steps, all simulated
- `worker.py`: the Worker on task queue `orders`
- `app.py` and `templates/index.html`: the web app
- `tests/test_order.py`: one test per scenario
