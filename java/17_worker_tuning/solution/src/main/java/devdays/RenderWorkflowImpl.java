package devdays;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RenderWorkflowImpl implements RenderWorkflow {
    private final RenderActivities activities = Workflow.newActivityStub(
            RenderActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(60)).build());

    @Override
    public List<String> run(List<String> items) {
        List<Promise<String>> promises = new ArrayList<>();
        for (String item : items) {
            promises.add(Async.function(activities::render, item));
        }
        Promise.allOf(promises).get();
        return promises.stream().map(Promise::get).toList();
    }
}
