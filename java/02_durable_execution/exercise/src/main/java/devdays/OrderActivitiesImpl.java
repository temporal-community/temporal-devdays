package devdays;

public class OrderActivitiesImpl implements OrderActivities {
    @Override
    public String takeOrder(String name) {
        return "ticket-" + name;
    }

    @Override
    public String bake(String ticket) {
        return "baked " + ticket;
    }
}
