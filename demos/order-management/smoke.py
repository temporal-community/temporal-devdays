"""Drive the running demo through its web app and check every scenario end to end.
Standard library only. Start the demo first (./run.sh python | java | mixed), then: ./run.sh smoke [--chaos]
--chaos also stops the payments service, places an order, checks it waits, and starts the service again."""
import json
import subprocess
import sys
import time
import urllib.error
import urllib.request

BASE = "http://localhost:5050"
HERE = __file__.rsplit("/", 1)[0] or "."
failures = []


def call(path, body=None):
    req = urllib.request.Request(BASE + path, data=None if body is None else json.dumps(body).encode(),
                                 headers={"Content-Type": "application/json"}, method="GET" if body is None else "POST")
    with urllib.request.urlopen(req, timeout=20) as reply:
        return json.loads(reply.read())


def place(scenario):
    return call("/start", {"scenario": scenario, "address": "12 Main Street"})["order_id"]


def wait(order, until, seconds=60):
    end, data = time.time() + seconds, {}
    while time.time() < end:
        data = call(f"/timeline/{order}")
        if until(data):
            return data
        time.sleep(0.25)
    return data


def names(data, **match):
    return [s["name"] for s in data["steps"] if all(s[k] == v for k, v in match.items())]


def check(label, ok, detail=""):
    print(("ok    " if ok else "FAIL  ") + label + ("" if ok else f"  {detail}"))
    if not ok:
        failures.append(label)


def completed(data):
    return data.get("state") == "COMPLETED"


def scenarios():
    services = call("/services")
    check("all four services have a Worker", all(s["up"] for s in services), services)
    print("      languages:", {s["name"]: s["language"] for s in services})

    d = wait(place("happy"), completed)
    child = next((s for s in d["steps"] if s["kind"] == "child"), {"items": [], "status": ""})
    check("happy path ships with a child Workflow and three items",
          names(d, kind="activity") == ["check_stock", "reserve_stock", "charge"] and child["status"] == "done" and len(child["items"]) == 3, d)

    order = place("flaky_card")
    d = wait(order, lambda x: any(s["status"] == "retrying" for s in x["steps"]), 20)
    retry = next((s for s in d["steps"] if s["status"] == "retrying"), None)
    check("flaky card shows a retry with attempt and error", bool(retry) and retry["attempt"] >= 2 and bool(retry["detail"]), d)
    d = wait(order, completed)
    check("flaky card succeeds on attempt 3", next((s for s in d["steps"] if s["name"] == "charge"), {}).get("attempt") == 3, d)

    d = wait(place("bad_card"), completed)
    check("bad card undoes in reverse and ships nothing",
          names(d, undo=True) == ["refund_charge", "release_stock"] and not any(s["kind"] == "child" for s in d["steps"]), d)

    d = wait(place("ship_fails"), completed)
    child = next((s for s in d["steps"] if s["kind"] == "child"), {"status": ""})
    check("shipping fails: child failed, refund then release", child["status"] == "failed" and names(d, undo=True) == ["refund_charge", "release_stock"], d)

    order = place("approval")
    d = wait(order, lambda x: x.get("countdown_ends") is not None, 20)
    check("approval waits with a countdown", d.get("countdown_ends") is not None, d)
    rejected = urllib.request.Request(BASE + f"/address/{order}", data=b'{"address":"x"}', headers={"Content-Type": "application/json"}, method="POST")
    try:
        urllib.request.urlopen(rejected, timeout=20)
        check("a too short address is rejected", False, "the Update was accepted")
    except urllib.error.HTTPError as e:
        check("a too short address is rejected", e.code == 400)
    call(f"/address/{order}", {"address": "99 Long Road, Pune"})
    call(f"/approve/{order}", {})
    d = wait(order, completed)
    check("approval, address Update and Signal complete the order",
          names(d, kind="update") == ["change_address"] and names(d, kind="signal") == ["approve"] and d["countdown_ends"] is None, d)

    recent = call("/orders")
    check("recent orders list scenarios", {"happy", "flaky_card", "bad_card", "ship_fails", "approval"} <= {o["scenario"] for o in recent}, recent)


def chaos():
    run = f"{HERE}/run.sh"
    try:
        language = open(f"{HERE}/.run/payments.lang").read().strip()   # restart it in the language it had
    except OSError:
        language = "python"
    subprocess.run([run, "stop", "payments"], check=True)
    order = place("happy")
    d = wait(order, lambda x: any(s["status"] == "waiting" for s in x["steps"]), 20)
    waiting = next((s for s in d["steps"] if s["status"] == "waiting"), {})
    check("with payments stopped the charge waits for a Worker", waiting.get("name") == "charge", d)
    subprocess.run([run, "start", "payments", language], check=True)
    d = wait(order, completed, 90)
    check("after payments is back the same order completes", completed(d) and names(d, kind="activity")[-1] == "charge", d)


if __name__ == "__main__":
    scenarios()
    if "--chaos" in sys.argv:
        chaos()
    print(f"\n{'FAILED: ' + ', '.join(failures) if failures else 'all checks passed'}")
    sys.exit(1 if failures else 0)
