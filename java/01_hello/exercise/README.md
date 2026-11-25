# Exercise 01: Hello, durable world

Run your first Workflow: it calls one Activity and returns a greeting.

[Concept](../../../concepts/01-hello.md) · Slides: `demo-first, write-activity, write-workflow, init-worker`

## Run the check
```bash
temporal server start-dev      # only needed for the live run below
cd java/01_hello/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`WorkerApp.java`): register the Workflow and the Activities on the Worker
- [ ] TODO 2 (`GreetSomeoneImpl.java`): call `activities.createGreeting(name)` and return the result

## The check
The Workflow returns `Hello, Temporal!`.

## Run it live
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter     # terminal 3
```

## Stretch
Change the greeting, restart the Worker and run the starter again. Find the run in the Web UI at http://localhost:8233.

Solution: [solution](../solution)
