package devdays;

public class ShipmentWorkflowImpl implements ShipmentWorkflow {
    @Override
    public String run(String orderId) {
        return "shipped " + orderId;
    }
}
