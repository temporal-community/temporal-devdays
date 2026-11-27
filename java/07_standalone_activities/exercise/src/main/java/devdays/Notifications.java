package devdays;

import io.temporal.client.ActivityClient;
import io.temporal.client.ActivityHandle;
import io.temporal.client.StartActivityOptions;
import java.time.Duration;

public class Notifications {
    public static final String TASK_QUEUE = "notifications";
    public static final String ACTIVITY_ID = "welcome-notification-user-123";
    static final SendNotificationInput INPUT = new SendNotificationInput("user-123", "Welcome!");

    public static String runNow(ActivityClient client) {
        // TODO 1 start: run sendNotification as a Standalone Activity with client.execute and return the result
        throw new UnsupportedOperationException("TODO 1: run sendNotification as a Standalone Activity with client.execute and return the result");
        // TODO 1 end
    }

    public static ActivityHandle<String> runLater(ActivityClient client) {
        return runLater(client, Duration.ofHours(6), ACTIVITY_ID);
    }

    public static ActivityHandle<String> runLater(ActivityClient client, Duration delay, String id) {
        // TODO 2 start: start it with client.start, setStartDelay(delay) and setId(id), and return the handle
        throw new UnsupportedOperationException("TODO 2: start it with client.start, setStartDelay(delay) and setId(id), and return the handle");
        // TODO 2 end
    }
}
