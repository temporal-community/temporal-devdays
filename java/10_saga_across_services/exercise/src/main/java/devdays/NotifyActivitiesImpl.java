package devdays;

public class NotifyActivitiesImpl implements NotifyActivities {
    @Override
    public void sendReceipt(TransferRequest req) {
        System.out.println("Receipt sent for " + req.amount());
    }
}
