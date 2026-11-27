package demo;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.PendingActivityState;
import io.temporal.api.enums.v1.TaskQueueType;
import io.temporal.api.history.v1.HistoryEvent;
import io.temporal.api.taskqueue.v1.TaskQueue;
import io.temporal.api.workflowservice.v1.DescribeTaskQueueRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Timeline {
    static final List<String> QUEUES = List.of("orders", "inventory", "payments", "shipping");
    static final List<String> UNDO = List.of("release_stock", "refund_charge");
    static final long FRESH_SECONDS = 60;  // an idle Worker asks for work again about every 50 seconds
    static final long WAIT_SECONDS = 3;    // a task nobody picks up for this long means no Worker is listening

    static String language(List<String> identities) {
        return identities.stream().filter(i -> i.startsWith("java-") || i.startsWith("python-"))
            .map(i -> i.split("-")[0]).findFirst().orElse("");
    }

    static String name(Enum<?> status, String prefix) {
        return status.name().replace(prefix, "");
    }

    static List<Map<String, Object>> services(WorkflowClient client) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (String queue : QUEUES) {
            var reply = client.getWorkflowServiceStubs().blockingStub().describeTaskQueue(DescribeTaskQueueRequest.newBuilder()
                .setNamespace(client.getOptions().getNamespace())
                .setTaskQueue(TaskQueue.newBuilder().setName(queue))
                .setTaskQueueType(queue.equals("orders") ? TaskQueueType.TASK_QUEUE_TYPE_WORKFLOW : TaskQueueType.TASK_QUEUE_TYPE_ACTIVITY)
                .build());
            long now = System.currentTimeMillis() / 1000;
            List<String> fresh = reply.getPollersList().stream()
                .filter(p -> now - p.getLastAccessTime().getSeconds() < FRESH_SECONDS)
                .map(p -> p.getIdentity()).toList();
            out.add(map("name", queue, "up", !fresh.isEmpty(), "pollers", fresh.size(), "language", language(fresh)));
        }
        return out;
    }

    static Map<String, Object> step(String kind, String name, String service, String detail, boolean undo) {
        return map("kind", kind, "name", name, "service", service, "status", "running", "attempt", 1,
            "detail", detail, "undo", undo, "worker", "", "language", "");
    }

    record Read(List<Map<String, Object>> steps, Long timer) {}

    static Read read(List<HistoryEvent> events) {
        List<Map<String, Object>> steps = new ArrayList<>();
        Map<Long, Map<String, Object>> byEvent = new HashMap<>();
        Map<String, Object> child = null;
        Long timer = null;
        for (HistoryEvent e : events) {
            switch (e.getEventType()) {
                case EVENT_TYPE_ACTIVITY_TASK_SCHEDULED -> {
                    var a = e.getActivityTaskScheduledEventAttributes();
                    String item = a.getInput().getPayloadsCount() > 1  // ship_item(order_id, item, scenario)
                        ? a.getInput().getPayloads(1).getData().toStringUtf8().replace("\"", "") : "";
                    Map<String, Object> s = step("activity", a.getActivityType().getName(), a.getTaskQueue().getName(),
                        item, UNDO.contains(a.getActivityType().getName()));
                    byEvent.put(e.getEventId(), s);
                    s.put("activity_id", a.getActivityId());  // used to match pending Activities, removed below
                    steps.add(s);
                }
                case EVENT_TYPE_ACTIVITY_TASK_STARTED -> {
                    var a = e.getActivityTaskStartedEventAttributes();
                    byEvent.get(a.getScheduledEventId()).putAll(Map.of("attempt", a.getAttempt(), "worker", a.getIdentity()));
                }
                case EVENT_TYPE_ACTIVITY_TASK_COMPLETED ->
                    byEvent.get(e.getActivityTaskCompletedEventAttributes().getScheduledEventId()).put("status", "done");
                case EVENT_TYPE_ACTIVITY_TASK_FAILED -> {
                    var a = e.getActivityTaskFailedEventAttributes();
                    byEvent.get(a.getScheduledEventId()).putAll(Map.of("status", "failed", "detail", a.getFailure().getMessage()));
                }
                case EVENT_TYPE_ACTIVITY_TASK_TIMED_OUT ->
                    byEvent.get(e.getActivityTaskTimedOutEventAttributes().getScheduledEventId()).putAll(Map.of("status", "failed", "detail", "timed out"));
                case EVENT_TYPE_CHILD_WORKFLOW_EXECUTION_STARTED -> {
                    var a = e.getChildWorkflowExecutionStartedEventAttributes();
                    child = step("child", a.getWorkflowType().getName(), "orders", a.getWorkflowExecution().getWorkflowId(), false);
                    steps.add(child);
                }
                case EVENT_TYPE_CHILD_WORKFLOW_EXECUTION_COMPLETED -> child.put("status", "done");
                case EVENT_TYPE_CHILD_WORKFLOW_EXECUTION_FAILED ->
                    child.putAll(Map.of("status", "failed", "detail", "a child step failed for good"));
                case EVENT_TYPE_WORKFLOW_EXECUTION_SIGNALED -> {
                    Map<String, Object> s = step("signal", e.getWorkflowExecutionSignaledEventAttributes().getSignalName(), "orders", "", false);
                    s.put("status", "done");
                    steps.add(s);
                }
                case EVENT_TYPE_WORKFLOW_EXECUTION_UPDATE_ACCEPTED -> {
                    Map<String, Object> s = step("update",
                        e.getWorkflowExecutionUpdateAcceptedEventAttributes().getAcceptedRequest().getInput().getName(), "orders", "", false);
                    s.put("status", "done");
                    steps.add(s);
                }
                case EVENT_TYPE_TIMER_STARTED ->
                    timer = e.getEventTime().getSeconds() + e.getTimerStartedEventAttributes().getStartToFireTimeout().getSeconds();
                case EVENT_TYPE_TIMER_FIRED, EVENT_TYPE_TIMER_CANCELED -> timer = null;
                default -> { }
            }
        }
        return new Read(steps, timer);
    }

    record Steps(String state, List<Map<String, Object>> steps, Long timer) {}

    static Steps stepsOf(WorkflowClient client, String workflowId) {
        DescribeWorkflowExecutionResponse description = client.getWorkflowServiceStubs().blockingStub()
            .describeWorkflowExecution(DescribeWorkflowExecutionRequest.newBuilder()
                .setNamespace(client.getOptions().getNamespace())
                .setExecution(WorkflowExecution.newBuilder().setWorkflowId(workflowId)).build());
        Read read = read(client.fetchHistory(workflowId).getEvents());
        long now = System.currentTimeMillis() / 1000;
        for (var p : description.getPendingActivitiesList()) {
            Map<String, Object> step = read.steps().stream().filter(s -> s.get("status").equals("running")
                && p.getActivityId().equals(s.get("activity_id"))).findFirst().orElse(null);
            if (step == null) {  // it finished between the two reads
                continue;
            }
            if (!p.getLastFailure().getMessage().isEmpty()) {
                step.putAll(Map.of("status", "retrying", "attempt", p.getAttempt(), "detail", p.getLastFailure().getMessage()));
            } else if (p.getState() == PendingActivityState.PENDING_ACTIVITY_STATE_SCHEDULED
                    && now - p.getScheduledTime().getSeconds() > WAIT_SECONDS) {
                step.putAll(Map.of("status", "waiting", "detail", "no Worker is picking this up on " + step.get("service")));
            }
        }
        for (Map<String, Object> s : read.steps()) {
            s.remove("activity_id");
            s.put("language", language(List.of((String) s.get("worker"))));
        }
        return new Steps(name(description.getWorkflowExecutionInfo().getStatus(), "WORKFLOW_EXECUTION_STATUS_"), read.steps(), read.timer());
    }

    static Map<String, Object> timeline(WorkflowClient client, String orderId) {
        Steps order = stepsOf(client, "order-" + orderId);
        for (Map<String, Object> s : order.steps()) {
            if (s.get("kind").equals("child")) {
                s.put("items", stepsOf(client, "ship-" + orderId).steps());
            }
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("state", order.state());
        out.put("steps", order.steps());
        out.put("countdown_ends", order.timer());
        return out;
    }

    static List<Map<String, Object>> orders(WorkflowClient client) {
        return client.listExecutions("WorkflowType='OrderWorkflow'").limit(20)
            .sorted(Comparator.comparing(io.temporal.client.WorkflowExecutionMetadata::getStartTime).reversed()).limit(10)
            .map(wf -> map("id", wf.getExecution().getWorkflowId().replaceFirst("^order-", ""),
                "scenario", String.valueOf(wf.getMemo("scenario", String.class)).replace("null", ""),
                "state", name(wf.getStatus(), "WORKFLOW_EXECUTION_STATUS_"), "started", wf.getStartTime().toString()))
            .toList();
    }

    static Map<String, Object> map(Object... pairs) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            out.put((String) pairs[i], pairs[i + 1]);
        }
        return out;
    }
}
