package devdays;

public class GreetingActivitiesImpl implements GreetingActivities {
    @Override
    public String createGreeting(String name) {
        return "Hello, " + name + "!";
    }
}
