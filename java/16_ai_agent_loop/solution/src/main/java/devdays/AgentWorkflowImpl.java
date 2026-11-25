package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AgentWorkflowImpl implements AgentWorkflow {
    private final AgentActivities tools = Workflow.newActivityStub(
            AgentActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public String run(String question) {
        List<Message> messages = new ArrayList<>(List.of(new Message("user", question)));
        for (int turn = 0; turn < 5; turn++) {
            // TODO 1 start [workflow]: ask the model with an llmDecide Activity retried at most 3 times: build the stub with RetryOptions, then call it
            AgentActivities llm = Workflow.newActivityStub(
                    AgentActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(5))
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setInitialInterval(Duration.ofMillis(10))
                                    .setMaximumAttempts(3)
                                    .build())
                            .build());
            Decision decision = llm.llmDecide(messages);
            //? Decision decision = MockLlm.decide(messages);
            // TODO 1 end
            if (!decision.answer().isEmpty()) {
                return decision.answer();
            }
            // TODO 2 start [workflow]: run the tool with tools.runTool and add its result as a tool message
            String result = tools.runTool(decision);
            //? String result = "sunny";
            // TODO 2 end
            messages.add(new Message("tool", result));
        }
        return "gave up";
    }
}
