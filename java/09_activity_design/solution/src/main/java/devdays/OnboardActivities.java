package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OnboardActivities {
    void addUser(String name);

    void sendWelcome(String name);

    void createUser(String name);
}
