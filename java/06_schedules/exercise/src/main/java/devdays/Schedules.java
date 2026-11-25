package devdays;

import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.Schedule;
import io.temporal.client.schedules.ScheduleActionStartWorkflow;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.client.schedules.ScheduleIntervalSpec;
import io.temporal.client.schedules.ScheduleOptions;
import io.temporal.client.schedules.ScheduleSpec;
import java.time.Duration;
import java.util.List;

public class Schedules {
    public static final String SCHEDULE_ID = "nightly-report";
    public static final String TASK_QUEUE = "reports";

    public static Schedule buildSchedule() {
        // TODO 1 start: return a Schedule that starts ReportWorkflow (id "report", task queue TASK_QUEUE) every minute
        throw new UnsupportedOperationException("TODO 1: return a Schedule that starts ReportWorkflow (id \"report\", task queue TASK_QUEUE) every minute");
        // TODO 1 end
    }

    public static void createSchedule(ScheduleClient client) {
        // TODO 2 start: create the Schedule with client.createSchedule, using SCHEDULE_ID and buildSchedule()
        throw new UnsupportedOperationException("TODO 2: create the Schedule with client.createSchedule, using SCHEDULE_ID and buildSchedule()");
        // TODO 2 end
    }
}
