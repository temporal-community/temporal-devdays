# Order management: Java services

The same four services as the [Python version](../python), written in Java 21. Read the [demo README](../README.md) first for the architecture and the web app. The web app is in `../python`.

## Services

| Service | Task queue | Classes |
|---|---|---|
| orders | `orders` | `OrderWorkflow`, `ShipmentWorkflow` |
| inventory | `inventory` | `InventoryActivities` |
| payments | `payments` | `PaymentActivities` |
| shipping | `shipping` | `ShippingActivities` |

Each Activity interface has an `Impl` class next to it. Names such as `check_stock`, `charge` and `change_address` are set explicitly, so the Java services work with the Python ones.

## Run

You need Java 21, Maven and the [Temporal CLI](https://docs.temporal.io/cli). Run these from `demos/order-management/java`.

```bash
temporal server start-dev                                  # terminal 1
cd ../python && uv run python app.py                       # terminal 2, open http://localhost:5050
```

Then the services, in the terminal you prefer:

```bash
mvn -q compile exec:java@worker                            # all four services in one process
mvn -q compile exec:java@worker -Dexec.args="payments"     # only payments
mvn -q compile exec:java@worker -Dexec.args="orders inventory payments shipping"
```

One Worker per queue. If a Python service already runs a queue, do not start the same one here.

To run every service in its own terminal, use one command per terminal:

```bash
mvn -q compile exec:java@worker -Dexec.args="orders"       # terminal 3
mvn -q compile exec:java@worker -Dexec.args="inventory"    # terminal 4
mvn -q compile exec:java@worker -Dexec.args="payments"     # terminal 5
mvn -q compile exec:java@worker -Dexec.args="shipping"     # terminal 6
```

## Test

No server needed.

```bash
mvn -q test
```

Seven tests, one per scenario: happy path, flaky card, bad card, approval Signal, approval timeout, address Update, and a rejected address.

## Where to look

- `WorkerApp.java`: which queue gets which service, and the `main` that starts them
- `OrderWorkflowImpl.java`: the order steps, Saga undo, Signal, Update and Query
- `ShipmentWorkflowImpl.java`: ships every item in parallel
- `Models.java`: the inputs and outputs, with the JSON field names the Python app uses
- `OrderTest.java`: the tests

If you change the code, stop and restart the Worker.
