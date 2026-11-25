package devdays;

import io.temporal.client.schedules.ScheduleClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class CreateSchedule {
    public static void main(String[] args) {
        ScheduleClient client = ScheduleClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        Schedules.createSchedule(client);
        System.out.println("Created " + Schedules.SCHEDULE_ID);
        System.exit(0);
    }
}
