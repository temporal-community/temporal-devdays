package devdays;

import io.temporal.failure.ApplicationFailure;

public class FraudActivitiesImpl implements FraudActivities {
    @Override
    public void checkFraud(TransferRequest req) {
        if (req.failAt().equals("fraud")) {
            throw ApplicationFailure.newNonRetryableFailure("flagged as fraud", "Fraud");
        }
    }
}
