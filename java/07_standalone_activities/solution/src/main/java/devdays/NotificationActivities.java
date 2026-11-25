package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface NotificationActivities {
    String sendNotification(SendNotificationInput input);
}
