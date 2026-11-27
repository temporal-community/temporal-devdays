# Runbook: presenting the order demo

How to show this demo in about ten minutes. [README.md](README.md) explains what it is. This page is what to click and what to say.

## What it shows

Three things that go wrong in real systems, and what Temporal does about each:

1. **A step fails now and then.** Temporal retries it. There is no retry loop in the code.
2. **A service is down.** The order waits and carries on when the service is back. Nothing is lost.
3. **Something fails for good.** The order undoes its earlier steps in reverse order, across services (a Saga).

And one more: the services can be written in **Python or Java** and still work together.

The thirty-second version: place a **Flaky card** order, watch the amber retry, then stop `payments`, place a **Happy path** order, watch it wait, start `payments` again and watch it finish.

## Before you start

```bash
cd demos/order-management
./run.sh python          # or: ./run.sh mixed (Java payments, Python everything else)
```

- Open the demo at http://localhost:5050 and the Temporal Web UI at http://localhost:8233, side by side.
- The four chips at the top should be green, each with a **PY** or **JAVA** badge.
- Keep a terminal open for `./run.sh stop payments` and `./run.sh start payments`.
- After a run: `./run.sh down`. It stops only what it started.

## Beat 1: the happy path (1 minute)

- **Click:** Happy path, then Place order.
- **Say:** "One click starts one Workflow. It calls four services and none of them calls another. Each step lands in the lane of the service that ran it."
- **For proof:** click *Order in Web UI* and read the history. Click *Shipment in Web UI*: the child Workflow `ship-<id>` ran three `ship_item` Activities at the same time.
- **Show it in code:** [`OrderWorkflow`](python/workflows.py) in Python, [`OrderWorkflowImpl`](java/src/main/java/demo/OrderWorkflowImpl.java) in Java.

## Beat 2: a flaky card (1 minute)

- **Click:** Flaky card, then Place order.
- **Say:** "The payment API fails twice. We wrote no retry code. Temporal retries, with a growing wait, and the third attempt works."
- **Watch:** the `charge` chip turns amber and reads *attempt 2 of 5* with the error, then green.
- **For proof:** in the Web UI open the `charge` Activity: three attempts, the first two with the error.
- **Show it in code:** the retry policy (`RetryPolicy`) next to the Activity calls in [`workflows.py`](python/workflows.py).

## Beat 3: a service goes down (2 minutes)

This is the strongest beat.

- **Do:** `./run.sh stop payments`
- **Click:** Happy path, then Place order.
- **Say:** "The payments service is gone. The order does not fail. It stops at the charge and waits."
- **Watch:** within about five seconds the `charge` chip turns purple, *waiting for a Worker*, and the narration says no Worker is listening on `payments`. The `payments` chip at the top turns red as soon as work is waiting for it. When nothing is waiting, the server only learns a Worker is gone after about a minute.
- **For proof:** in the Web UI open Task Queues and `payments`: no Workers polling.
- **Do:** `./run.sh start payments`
- **Say:** "Same order, same ID. Nobody retried anything by hand. It carries on and ships."
- **Show it in code:** nothing. There is no code for this. That is the point.

## Beat 4: a bad card (1 minute)

- **Click:** Bad card, then Place order.
- **Say:** "A declined card must not be retried. It is a final answer. The order undoes what it did so far."
- **Watch:** `charge` fails once, then two dashed undo chips: `refund_charge`, then `release_stock`.
- **Say:** "The refund finds nothing to refund and does nothing. An undo step must be safe to run when there is nothing to undo, because the charge can land even when the call fails."
- **Show it in code:** the undo list in [`workflows.py`](python/workflows.py): the undo is added before the step.

## Beat 5: shipping fails, a full Saga (2 minutes)

- **Click:** Shipping fails, then Place order.
- **Say:** "The card is charged. Then one item cannot ship. The child Workflow fails, and the order undoes its steps in reverse: refund the card, then release the stock."
- **Watch:** `keypad` fails with *no courier available*, `ShipmentWorkflow` turns red, then the two hatched undo chips appear in reverse order.
- **For proof:** in the Web UI the order history shows the child failure, then `refund_charge`, then `release_stock`.

## Beat 6: waiting for a person (1 minute)

- **Click:** Needs approval, then Place order.
- **Say:** "The Workflow waits for a person. It holds no thread, and it will time out in an hour. While it waits, we can change the address with an Update, and approve with a Signal."
- **Click:** *too short (rejected)* then Change: the Update is rejected. Then *99 Long Road, Pune* and Change: accepted. Then Approve.
- **Watch:** the countdown, the *update* and *signal* chips, then the order ships to the new address.

## Beat 7: Python and Java together (1 minute)

- **Do:** `./run.sh stop payments` then `./run.sh start payments java`
- **Click:** Happy path, then Place order.
- **Say:** "payments is a Java service now. The other three are Python. The task queue names and the Activity names are the whole contract."
- **Watch:** the `payments` chip and the `charge` step say **JAVA**, the others say **PY**.

## If something goes wrong

| What you see | What to do |
|---|---|
| The top right says *stale* | The web app stopped. `./run.sh status`, then `./run.sh logs`. |
| A chip is red but you started the service | Wait a few seconds. It turns green when the Worker asks for work. |
| A chip stays green after `stop` | Normal when nothing is waiting: it takes up to a minute. Place an order and the step shows *waiting for a Worker* at once. |
| Port 5050 is in use | Another program has it. macOS AirPlay uses 5000, which is why this demo uses 5050. |
| Nothing happens after Place order | `./run.sh status`: is `orders` running? The order Workflow needs it. |
| You want a clean slate | `./run.sh down`, then start again. The Temporal dev server forgets everything when it is stopped. |
