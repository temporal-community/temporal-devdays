# Exercise 16: An AI agent loop

Run an agent loop where the model and the tool are Activities, using a fake model.

[Concept](../../../concepts/16-ai-agent-loop.md) · Slides: `ai-skill, demo-ai`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd java/16_ai_agent_loop/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`AgentWorkflowImpl.java`): ask the model with an `llmDecide` Activity retried at most 3 times
- [ ] TODO 2 (`AgentWorkflowImpl.java`): run the tool with the `runTool` Activity

## The check
The answer is `It is sunny in Pune`, the history shows two model Activities and one tool Activity, and the model Activity retries at most 3 times.

## Run it live
```bash
mvn -q compile exec:java@worker      # terminal 2
mvn -q compile exec:java@starter     # terminal 3
```

## Stretch
Change the fake model to ask for a second tool call before answering.

Solution: [solution](../solution)
