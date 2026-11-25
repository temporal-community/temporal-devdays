package devdays;

import io.temporal.activity.Activity;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OnboardActivitiesImpl implements OnboardActivities {
    public static final List<String> users = new CopyOnWriteArrayList<>();
    public static final List<String> emails = new CopyOnWriteArrayList<>();

    private static int attempt() {
        return Activity.getExecutionContext().getInfo().getAttempt();
    }

    @Override
    public void addUser(String name) {
        users.add(name);
    }

    @Override
    public void sendWelcome(String name) {
        if (attempt() == 1) {
            throw new RuntimeException("email service is down");
        }
        emails.add(name);
    }

    @Override
    public void createUser(String name) {
        users.add(name);
        if (attempt() == 1) {
            throw new RuntimeException("email service is down");
        }
        emails.add(name);
    }
}
