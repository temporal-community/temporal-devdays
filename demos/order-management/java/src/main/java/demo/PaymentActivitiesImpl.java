package demo;

import demo.Models.OrderInput;
import io.temporal.activity.Activity;
import io.temporal.failure.ApplicationFailure;

public class PaymentActivitiesImpl implements PaymentActivities {
    @Override
    public void charge(OrderInput order) {
        Calls.all.add("charge");
        if (order.scenario().equals("bad_card")) {
            throw ApplicationFailure.newNonRetryableFailure("card declined", "CardDeclined");
        }
        int attempt = Activity.getExecutionContext().getInfo().getAttempt();
        if (order.scenario().equals("flaky_card") && attempt < 3) {
            throw new RuntimeException("payment API unavailable");
        }
        Calls.charged.add(order.orderId());
    }

    @Override
    public void refundCharge(OrderInput order) {
        if (Calls.charged.remove(order.orderId())) {   // safe to run when nothing was charged, or twice
            Calls.all.add("refund_charge");
        } else {
            Calls.all.add("refund_skipped");
        }
    }
}
