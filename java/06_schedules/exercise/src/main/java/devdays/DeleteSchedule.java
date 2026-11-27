package devdays;

import io.temporal.client.schedules.ScheduleClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class DeleteSchedule {
    public static void main(String[] args) {
        ScheduleClient client = ScheduleClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        Schedules.deleteSchedule(client);
        System.out.println("Deleted " + Schedules.SCHEDULE_ID);
        System.exit(0);
    }
}
