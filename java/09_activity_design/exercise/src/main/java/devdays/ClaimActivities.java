package devdays;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ClaimActivities {
    String evaluateClaim(Claim claim);
}
