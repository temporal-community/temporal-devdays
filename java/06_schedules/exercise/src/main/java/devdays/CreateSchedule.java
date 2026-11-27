package devdays;

import io.temporal.client.schedules.ScheduleClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class CreateSchedule {
    public static void main(String[] args) {
        ScheduleClient client = ScheduleClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        Schedules.createSchedule(client);
        System.out.println("Created " + Schedules.SCHEDULE_ID);
        System.out.println("It stops after 5 runs. To stop sooner: mvn -q compile exec:java@delete");
        System.exit(0);
    }
}
