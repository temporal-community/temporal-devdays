package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import java.util.List;
import org.junit.jupiter.api.Test;

class SagaTest {
    private String run(String failAt) {
        AccountsActivitiesImpl.withdrawn.clear();
        AccountsActivitiesImpl.undone.clear();
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            OrchestratorWorker.register(env.newWorker(OrchestratorWorker.TASK_QUEUE));
            AccountsWorker.register(env.newWorker(AccountsWorker.TASK_QUEUE));
            FraudWorker.register(env.newWorker(FraudWorker.TASK_QUEUE));
            NotifyWorker.register(env.newWorker(NotifyWorker.TASK_QUEUE));
            env.start();
            TransferWorkflow workflow = env.getWorkflowClient().newWorkflowStub(
                    TransferWorkflow.class,
                    WorkflowOptions.newBuilder().setTaskQueue(OrchestratorWorker.TASK_QUEUE).build());
            return workflow.run(new TransferRequest("alice", "bob", 100, failAt));
        } finally {
            env.close();
        }
    }

    @Test
    void happyPathHasNothingToUndo() {
        assertEquals("Transfer completed", run(""));
        assertEquals(List.of(), AccountsActivitiesImpl.undone);
    }

    @Test
    void depositFailureUndoesTheWithdrawal() {
        assertEquals("Transfer failed, money returned", run("deposit"));
        assertEquals(List.of("alice"), AccountsActivitiesImpl.undone, "TODO 2: run the undo steps when a step fails");
    }

    @Test
    void fraudFailureHasNothingToUndo() {
        assertEquals("Transfer failed, money returned", run("fraud"));
        assertEquals(List.of(), AccountsActivitiesImpl.undone);
    }

    @Test
    void aWithdrawalThatLandsAndThenFailsIsStillUndone() {
        assertEquals("Transfer failed, money returned", run("withdraw"));
        assertEquals(List.of("alice"), AccountsActivitiesImpl.undone,
                "TODO 1: register the undo BEFORE the step that can fail");
    }
}
