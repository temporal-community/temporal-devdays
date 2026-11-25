package devdays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import java.util.List;
import org.junit.jupiter.api.Test;

class PizzaWorkflowTest {
    private final PizzaOrder order = new PizzaOrder(
            new Customer("c1"), List.of(new Pizza("Margherita", 12)), "1 Main St", true, 1800);

    private String run(PizzaActivities activities) {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            Worker worker = env.newWorker("pizza");
            worker.registerWorkflowImplementationTypes(PizzaWorkflowImpl.class);
            worker.registerActivitiesImplementations(activities);
            env.start();
            PizzaWorkflow workflow = env.getWorkflowClient().newWorkflowStub(
                    PizzaWorkflow.class,
                    WorkflowOptions.newBuilder().setTaskQueue("pizza").build());
            return workflow.run(order);
        } finally {
            env.close();
        }
    }

    @Test
    void thirtyMinuteTimerFinishesInstantly() {
        // TODO 2 start: run the order with run(new PizzaActivitiesImpl()), check the bill starts with TPD and that it took under 20 seconds
        long started = System.nanoTime();
        String result = run(new PizzaActivitiesImpl());
        assertTrue(result.startsWith("TPD"));
        assertTrue((System.nanoTime() - started) / 1_000_000_000L < 20);
        // TODO 2 end
    }

    @Test
    void tooFarFailsTheWorkflow() {
        // TODO 3 start: run the order with an Activity that returns a distance of 40 and expect a WorkflowFailedException
        PizzaActivities farAway = new PizzaActivitiesImpl() {
            @Override
            public int getDistance(String address) {
                return 40;
            }
        };
        assertThrows(WorkflowFailedException.class, () -> run(farAway));
        // TODO 3 end
    }
}
