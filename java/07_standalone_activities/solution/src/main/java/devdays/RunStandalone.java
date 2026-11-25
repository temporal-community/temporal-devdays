package devdays;

import io.temporal.client.ActivityClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class RunStandalone {
    public static void main(String[] args) {
        ActivityClient client = ActivityClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        System.out.println(Notifications.runNow(client));
        System.exit(0);
    }
}
