# Demo: order management with four services

A small web app places an order. The order runs through **four independent services**, each with its own Worker and task queue. Temporal runs the order Workflow and calls the other three. You can run each service in Python or in Java, and mix them.

Ideas from exercises [03](../../concepts/03-retries.md), [04](../../concepts/04-messages.md), [10](../../concepts/10-saga-across-services.md), [12](../../concepts/12-human-in-the-loop.md) and [13](../../concepts/13-fan-out-fan-in.md).

Inspired by [temporal-order-management-demo](https://github.com/temporal-sa/temporal-order-management-demo). This is a new, smaller implementation, not a copy.

## How the services fit together

```mermaid
flowchart LR
    UI["Web app :5050"] -->|start, Signal, Update, Query| T[("Temporal")]
    T <-->|orders queue| O["orders service<br/>OrderWorkflow, ShipmentWorkflow"]
    T <-->|inventory queue| I["inventory service<br/>check_stock, reserve_stock, release_stock"]
    T <-->|payments queue| P["payments service<br/>charge, refund_charge"]
    T <-->|shipping queue| S["shipping service<br/>ship_item"]
```

| Service | Task queue | Holds | Kind | Called by |
|---|---|---|---|---|
| orders | `orders` | `OrderWorkflow`, `ShipmentWorkflow` | 2 Workflows, no Activities | the web app |
| inventory | `inventory` | `check_stock`, `reserve_stock`, `release_stock` | 3 Activities | `OrderWorkflow` |
| payments | `payments` | `charge`, `refund_charge` | 2 Activities | `OrderWorkflow` |
| shipping | `shipping` | `ship_item` | 1 Activity | `ShipmentWorkflow`, once per item in parallel |

A service is a Worker listening on one task queue. It holds Activities (or, for `orders`, Workflows). A service is not itself an Activity.

### The words used here

| Word | Meaning in this demo | Example |
|---|---|---|
| Workflow | the plan for one order. It decides what happens next and does no real work itself | `OrderWorkflow` |
| Child Workflow | a Workflow started by another Workflow, with its own history | `ShipmentWorkflow`, ID `ship-<order id>` |
| Activity | one step that does real work. It can fail and be retried | `charge`, `ship_item` |
| Service | a Worker plus its task queue | `payments` |
| Task queue | the mailbox Temporal uses to hand work to a service | `payments` |
| Worker | the program that listens at a queue and runs the work | each terminal window |
| Signal | a message that tells a running Workflow something, no answer back | `approve` |
| Update | a message that changes a running Workflow and returns an answer, and can be rejected | `change_address` |
| Query | a read-only question to a running Workflow | `status` |
| Retry Policy | how Temporal retries a failed Activity | `charge` fails twice, then passes |
| Saga | undoing earlier steps, in reverse order, when a later step fails | `refund_charge` then `release_stock` after shipping fails |

### One order, step by step

| # | What happens | Runs in | Queue | Kind |
|---|---|---|---|---|
| 1 | the web app starts the order | web app, then Temporal | none | start Workflow |
| 2 | check stock | inventory | `inventory` | Activity |
| 3 | reserve stock | inventory | `inventory` | Activity |
| 4 | wait for approval (**Needs approval** only) | `orders` | none | Workflow waits for a Signal |
| 5 | charge the card | payments | `payments` | Activity |
| 6 | start the shipment | `orders` | `orders` | child Workflow |
| 7 | ship the 3 items at the same time | shipping | `shipping` | 3 Activities |
| 8 | the order is done | `orders` | none | Workflow returns |

If step 5 fails for good (a declined card), step 3 is undone with `release_stock` and steps 6 and 7 never run, so no `ship-<id>` appears.

If step 7 fails (**Shipping fails**), the child Workflow fails and the order undoes its steps in reverse: `refund_charge` on `payments`, then `release_stock` on `inventory`. Here `ship-<id>` exists and shows as failed.

One simplification: `refund_charge` is registered after the charge succeeds, because a declined card moves no money. A production system registers it first, and the refund checks whether a charge exists, because a charge can land while the reply is lost (concept 10).

What this shows about microservices:
- **Services never call each other.** The Workflow calls each one by its task queue, so there is no service-to-service HTTP, no queue wiring and no retry code in the services.
- **A service can be down without losing the order.** Stop `payments` and an order waits at "charging card". Start it again and the order carries on. Try it below.
- **Each service can use a different language.** The queue and Activity names are the contract. Python and Java services work together.
- **Undo is the Workflow's job.** If something fails, the Workflow tells the earlier services to undo their work, in reverse order (Saga, as in exercise 10). Try **Shipping fails**: the card is refunded, then the stock is released.

## Run it

You need the [Temporal CLI](https://docs.temporal.io/cli), and either Python 3.11+ with [uv](https://docs.astral.sh/uv/), or Java 21 with Maven.

**Terminal 1: Temporal**
```bash
temporal server start-dev              # Web UI at http://localhost:8233
```

**Terminal 2: the web app.** Pick one. They serve the same page on port 5050.
```bash
cd demos/order-management/python && uv run python app.py     # Python
cd demos/order-management/java && mvn -q compile exec:java@app   # Java
```
Open http://localhost:5050. The web app is only a Temporal client, so it works with Workers in either language.

**Terminal 3: the services.** Pick one.

| | Python | Java |
|---|---|---|
| All four in one process | `uv run python worker.py` | `mvn -q compile exec:java@worker` |
| Only some | `uv run python worker.py payments shipping` | `mvn -q compile exec:java@worker -Dexec.args="payments shipping"` |

Run Python commands from `demos/order-management/python` and Java commands from `demos/order-management/java`. Each queue should have one Worker, so do not start the same service in both languages.

## Run each service on its own (the microservices way)

Use one terminal per service. This mixes the languages: three in Python, `payments` in Java.

```bash
cd demos/order-management/python
uv run python worker.py orders         # terminal 3
uv run python worker.py inventory      # terminal 4
uv run python worker.py shipping       # terminal 5

cd demos/order-management/java
mvn -q compile exec:java@worker -Dexec.args="payments"    # terminal 6
```

## Try this: a service goes down

1. Start all four services and the web app. Place a **Happy path** order. It ships.
2. Stop `payments` (Ctrl+C in its terminal).
3. Place another order. It stops at **Charge card** and stays running. In the Web UI the `charge` Activity is scheduled and waiting, with no Worker on `payments`.
4. Start `payments` again. The same order finishes. Nothing was lost and nothing was retried by hand.

## Scenarios

| Pick | What happens | What it teaches | Where to look in the Web UI |
|---|---|---|---|
| Happy path | stock, charge, then a child Workflow ships the three items in parallel | services work together without calling each other | `order-<id>` and its child `ship-<id>` |
| Flaky card | the charge fails twice, the Retry Policy makes attempt 3 pass | Temporal retries for you, no retry loop in your code | the `charge` Activity shows 3 attempts |
| Bad card | non-retryable error, the reserved stock is released, order cancelled | some errors must not be retried, so earlier steps are undone (Saga) | `release_stock` runs after `charge` fails |
| Needs approval | the Workflow waits for a Signal, an Update can change the address first | a Workflow can wait for a person without using a thread | click Approve, or Change address (a short address is rejected) |
| Shipping fails | the card is charged, then one item cannot ship, so the child Workflow fails | a full Saga: undo in reverse order, `refund_charge` then `release_stock` | the order's history, and `ship-<id>` showing Failed |

## Test

No server needed. Both run the same eight scenario tests (one per scenario, plus the approval timeout, the address Update and a rejected address), and the Java project also has three web app tests.

```bash
cd demos/order-management/python && uv run pytest -q
cd demos/order-management/java && mvn -q test
```

## Troubleshooting

- **Order stuck at the first step:** no Worker is polling the `orders` queue. Start the services. The Web UI shows "No workers polling" for that queue.
- **Order stuck at one step:** the service for that step is not running. Check the queue in the Web UI under Task Queues.
- **Port 5050 in use:** macOS uses port 5000 for AirPlay, so the app uses 5050.
- **Edited the code and nothing changed:** restart the Workers. Running Workers keep the old code.

## Files

- `python/workflows.py`: `OrderWorkflow` and `ShipmentWorkflow`, with the task queue on every Activity call
- `python/activities.py`: the services' Activities, all simulated
- `python/worker.py`: one Worker per service, `SERVICES` lists them
- `python/app.py`, `python/templates/index.html`: the web app
- `java/src/main/java/demo`: the same services and web app in Java, one interface and class per service
- `python/tests`, `java/src/test`: one test per scenario
