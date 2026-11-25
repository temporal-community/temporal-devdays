package devdays;

public class PizzaActivitiesImpl implements PizzaActivities {
    @Override
    public int getDistance(String address) {
        return 10;
    }

    @Override
    public String sendBill(Bill bill) {
        return "TPD-" + bill.customerId() + "-" + bill.amount();
    }
}
