package devdays;

import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.Schedule;
import io.temporal.client.schedules.ScheduleActionStartWorkflow;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.client.schedules.ScheduleIntervalSpec;
import io.temporal.client.schedules.ScheduleOptions;
import io.temporal.client.schedules.ScheduleSpec;
import io.temporal.client.schedules.ScheduleState;
import java.time.Duration;
import java.util.List;

public class Schedules {
    public static final String SCHEDULE_ID = "nightly-report";
    public static final String TASK_QUEUE = "reports";

    public static Schedule buildSchedule() {
        // TODO 1 start: return a Schedule that starts ReportWorkflow (id "report", task queue TASK_QUEUE) every minute, limited to 5 runs (ScheduleState)
        return Schedule.newBuilder()
                .setAction(ScheduleActionStartWorkflow.newBuilder()
                        .setWorkflowType(ReportWorkflow.class)
                        .setOptions(WorkflowOptions.newBuilder()
                                .setWorkflowId("report")
                                .setTaskQueue(TASK_QUEUE)
                                .build())
                        .build())
                .setSpec(ScheduleSpec.newBuilder()
                        .setIntervals(List.of(new ScheduleIntervalSpec(Duration.ofMinutes(1))))
                        .build())
                .setState(ScheduleState.newBuilder().setLimitedAction(true).setRemainingActions(5).build())
                .build();
        // TODO 1 end
    }

    public static void createSchedule(ScheduleClient client) {
        // TODO 2 start: create the Schedule with client.createSchedule, using SCHEDULE_ID and buildSchedule()
        client.createSchedule(SCHEDULE_ID, buildSchedule(), ScheduleOptions.newBuilder().build());
        // TODO 2 end
    }

    public static void deleteSchedule(ScheduleClient client) {
        client.getHandle(SCHEDULE_ID).delete();
    }
}
