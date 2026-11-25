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
        return client.execute(
                NotificationActivities.class,
                NotificationActivities::sendNotification,
                StartActivityOptions.newBuilder()
                        .setId(ACTIVITY_ID)
                        .setTaskQueue(TASK_QUEUE)
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .build(),
                INPUT);
        // TODO 1 end
    }

    public static ActivityHandle<String> runLater(ActivityClient client) {
        // TODO 2 start: start it with client.start and a start delay of 6 hours, and return the handle
        return client.start(
                NotificationActivities.class,
                NotificationActivities::sendNotification,
                StartActivityOptions.newBuilder()
                        .setId(ACTIVITY_ID)
                        .setTaskQueue(TASK_QUEUE)
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .setScheduleToCloseTimeout(Duration.ofDays(7))
                        .setStartDelay(Duration.ofHours(6))
                        .build(),
                INPUT);
        // TODO 2 end
    }
}
