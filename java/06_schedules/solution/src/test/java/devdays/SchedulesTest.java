package devdays;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.temporal.client.schedules.Schedule;
import io.temporal.client.schedules.ScheduleActionStartWorkflow;
import io.temporal.client.schedules.ScheduleClient;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SchedulesTest {
    @Test
    void buildsTheSchedule() {
        Schedule schedule = Schedules.buildSchedule();
        ScheduleActionStartWorkflow action = (ScheduleActionStartWorkflow) schedule.getAction();
        assertEquals("ReportWorkflow", action.getWorkflowType());
        assertEquals("report", action.getOptions().getWorkflowId());
        assertEquals("reports", action.getOptions().getTaskQueue());
        assertEquals(Duration.ofMinutes(1), schedule.getSpec().getIntervals().get(0).getEvery());
        assertEquals(5, schedule.getState().getRemainingActions());
    }

    @Test
    void createsItUnderTheRightId() {
        List<String> ids = new ArrayList<>();
        ScheduleClient client = (ScheduleClient) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] {ScheduleClient.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("createSchedule")) {
                        ids.add((String) args[0]);
                    }
                    return null;
                });
        Schedules.createSchedule(client);
        assertEquals(List.of("nightly-report"), ids);
    }
}
