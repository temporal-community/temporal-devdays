package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.temporal.client.ActivityClient;
import io.temporal.client.StartActivityOptions;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationsTest {
    private final List<String> calls = new ArrayList<>();
    private StartActivityOptions options;
    private Object input;

    private ActivityClient fakeClient() {
        return (ActivityClient) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] {ActivityClient.class},
                (proxy, method, args) -> {
                    calls.add(method.getName());
                    options = (StartActivityOptions) args[2];
                    input = args[3];
                    return method.getName().equals("execute") ? "sent" : null;
                });
    }

    @Test
    void runNowExecutesTheActivity() {
        assertEquals("sent", Notifications.runNow(fakeClient()));
        assertEquals("execute", calls.get(0));
        assertEquals(new SendNotificationInput("user-123", "Welcome!"), input);
        assertEquals("welcome-notification-user-123", options.getId());
        assertEquals("notifications", options.getTaskQueue());
        assertEquals(Duration.ofSeconds(30), options.getStartToCloseTimeout());
    }

    @Test
    void runLaterStartsItWithADelay() {
        Notifications.runLater(fakeClient());
        assertEquals("start", calls.get(0));
        assertEquals(Duration.ofHours(6), options.getStartDelay());
        assertEquals(Duration.ofDays(7), options.getScheduleToCloseTimeout());
    }

    @Test
    void runLaterUsesTheDelayAndIdItIsGiven() {
        Notifications.runLater(fakeClient(), Duration.ofSeconds(10), "other-id");
        assertEquals(Duration.ofSeconds(10), options.getStartDelay());
        assertEquals("other-id", options.getId());
    }
}
