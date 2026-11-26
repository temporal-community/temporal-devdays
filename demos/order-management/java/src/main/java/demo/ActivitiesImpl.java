package demo;

import demo.Models.OrderInput;
import io.temporal.activity.Activity;
import io.temporal.failure.ApplicationFailure;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ActivitiesImpl implements Activities {
    public static final List<String> calls = new CopyOnWriteArrayList<>();  // lets the tests see what ran

    @Override
    public List<String> checkStock(OrderInput order) {
        calls.add("check_stock");
        return List.of("table top", "table legs", "keypad");
    }

    @Override
    public void reserveStock(OrderInput order) {
        calls.add("reserve_stock");
    }

    @Override
    public void releaseStock(OrderInput order) {
        calls.add("release_stock");
    }

    @Override
    public void charge(OrderInput order) {
        calls.add("charge");
        if (order.scenario().equals("bad_card")) {
            throw ApplicationFailure.newNonRetryableFailure("card declined", "CardDeclined");
        }
        int attempt = Activity.getExecutionContext().getInfo().getAttempt();
        if (order.scenario().equals("flaky_card") && attempt < 3) {
            throw new RuntimeException("payment API unavailable");
        }
    }

    @Override
    public void shipItem(String orderId, String item) {
        calls.add("ship:" + item);
    }
}
