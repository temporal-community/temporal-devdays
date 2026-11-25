package devdays;

import java.util.concurrent.atomic.AtomicInteger;

public class KitchenActivitiesImpl implements KitchenActivities {
    // lets the check see how many ran at once
    public static final AtomicInteger running = new AtomicInteger();
    public static final AtomicInteger maxRunning = new AtomicInteger();

    @Override
    public String makeItem(String item) {
        maxRunning.accumulateAndGet(running.incrementAndGet(), Math::max);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        running.decrementAndGet();
        return item + " ready";
    }
}
