package devdays;

public class NotificationActivitiesImpl implements NotificationActivities {
    @Override
    public String sendNotification(SendNotificationInput input) {
        return "sent '" + input.message() + "' to " + input.userId();
    }
}
