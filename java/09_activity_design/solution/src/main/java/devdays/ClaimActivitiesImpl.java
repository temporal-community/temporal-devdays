package devdays;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClaimActivitiesImpl implements ClaimActivities {
    // lets the check see which Activities ran
    public static final List<String> calls = new CopyOnWriteArrayList<>();

    @Override
    public String evaluateClaim(Claim claim) {
        calls.add("evaluateClaim");
        return claim.amount() > 10000 || claim.kind().equals("surgery") ? "review" : "approve";
    }
}
