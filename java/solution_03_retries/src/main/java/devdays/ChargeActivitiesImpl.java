package devdays;

import io.temporal.activity.Activity;
import io.temporal.failure.ApplicationFailure;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChargeActivitiesImpl implements ChargeActivities {
    // lets the check count attempts
    public static final List<String> calls = new CopyOnWriteArrayList<>();

    @Override
    public String charge(String card) {
        calls.add(card);
        if (card.equals("bad")) {
            // TODO 2 start: throw ApplicationFailure.newNonRetryableFailure("card declined", "InvalidCard")
            throw ApplicationFailure.newNonRetryableFailure("card declined", "InvalidCard");
            // TODO 2 end
        }
        int attempt = Activity.getExecutionContext().getInfo().getAttempt();
        if (card.equals("flaky") && attempt < 3) {
            throw new RuntimeException("temporary failure");
        }
        return "charged on attempt " + attempt;
    }
}
