package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.WorkerOptions;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class TuningTest {
    private static final List<String> ITEMS = IntStream.range(0, 12).mapToObj(i -> "item-" + i).toList();

    private double run(WorkerOptions options) {
        RenderActivitiesImpl.running.set(0);
        RenderActivitiesImpl.maxRunning.set(0);
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            WorkerApp.register(env.newWorker(WorkerApp.TASK_QUEUE, options));
            env.start();
            RenderWorkflow workflow = env.getWorkflowClient().newWorkflowStub(
                    RenderWorkflow.class,
                    WorkflowOptions.newBuilder().setWorkflowId("t1").setTaskQueue(WorkerApp.TASK_QUEUE).build());
            long start = System.nanoTime();
            workflow.run(ITEMS);
            return (System.nanoTime() - start) / 1e9;
        } finally {
            env.close();
        }
    }

    @Test
    void activitySlotsCapHowManyRunAtOnce() {
        run(WorkerOptions.newBuilder().setMaxConcurrentActivityExecutionSize(Tuning.maxSlots()).build());
        assertEquals(3, RenderActivitiesImpl.maxRunning.get(),
                "TODO 1: return 3, the most Activities this Worker may run at once");
    }

    @Test
    void theRateLimitSpreadsTheStartsOverTime() {
        double elapsed = run(WorkerOptions.newBuilder().setMaxWorkerActivitiesPerSecond(Tuning.maxPerSecond()).build());
        assertTrue(elapsed > 1.8, "TODO 2: return 5, the most Activities this Worker may start per second");
    }
}
