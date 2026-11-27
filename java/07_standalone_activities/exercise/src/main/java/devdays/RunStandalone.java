package devdays;

import io.temporal.client.ActivityClient;
import io.temporal.client.ActivityHandle;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.time.Duration;

public class RunStandalone {
    public static void main(String[] args) {
        ActivityClient client = ActivityClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        System.out.println(Notifications.runNow(client));
        ActivityHandle<String> handle = Notifications.runLater(client, Duration.ofSeconds(10), Notifications.ACTIVITY_ID + "-later");
        System.out.println("scheduled, runs in 10 seconds");
        System.out.println(handle.getResult());
        System.exit(0);
    }
}
