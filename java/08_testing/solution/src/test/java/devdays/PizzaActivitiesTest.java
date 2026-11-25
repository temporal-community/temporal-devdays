package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.temporal.activity.ActivityOptions;
import io.temporal.testing.TestActivityEnvironment;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class PizzaActivitiesTest {
    @Test
    void sendBill() {
        // TODO 1 start: run sendBill with a TestActivityEnvironment and check the result is TPD-c1-12
        TestActivityEnvironment env = TestActivityEnvironment.newInstance();
        env.registerActivitiesImplementations(new PizzaActivitiesImpl());
        PizzaActivities activities = env.newActivityStub(
                PizzaActivities.class,
                ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());
        assertEquals("TPD-c1-12", activities.sendBill(new Bill("c1", 12)));
        // TODO 1 end
    }
}
