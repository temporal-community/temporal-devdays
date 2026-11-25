package devdays;

public class OrderActivitiesImpl implements OrderActivities {
    @Override
    public String takeOrder(String name) {
        return "ticket-" + name;
    }

    @Override
    public String checkStock(String ticket) {
        return "stock ok";
    }

    @Override
    public String bake(String ticket) {
        return "baked " + ticket;
    }
}
