# 05: Event History and replay

## The problem
A Workflow ran for two days and then your Worker was replaced. How does the new Worker know where the old one stopped? And how can you be sure a code change will not break Workflows that are still running?

## The idea
Every step a Workflow takes is written to an **Event History**, a log you can read. To continue a Workflow, a Worker **replays** it: it runs your code again from the top and checks each step against the history. Finished steps return the recorded result instead of running again. If your code now does something different from the history, replay fails, and that is how you catch a risky change before it ships.

## Picture
```
history:  Started → ActivityScheduled → ActivityCompleted → TimerStarted → ...
code:     get_distance()  →  sleep()  →  send_bill()
replay:   each line must match the next event in the log
```

## Use it when
- You want to see exactly what happened in a Workflow (the Web UI shows the history).
- You change Workflow code and want to know if running Workflows would break.

## Don't use it when
- You want to test business logic only. A normal unit test is faster.

## Common mistake
Reordering steps in a Workflow that is already running. The history says "Activity, then Timer" and the new code says "Timer, then Activity". Replay fails. Test with a recorded history before you deploy.

## Learn more
- [Event History](https://docs.temporal.io/encyclopedia/event-history)
- [Workflow definition and determinism](https://docs.temporal.io/workflow-definition)
- [Python: testing suite and replay](https://docs.temporal.io/develop/python/testing-suite)
- [Java: testing suite and replay](https://docs.temporal.io/develop/java/testing-suite)
