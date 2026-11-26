package demo;

import demo.Models.OrderInput;
import demo.Models.OrderOutput;
import demo.Models.OrderStatus;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.UpdateMethod;
import io.temporal.workflow.UpdateValidatorMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {
    @WorkflowMethod(name = "OrderWorkflow")
    OrderOutput run(OrderInput order);

    @SignalMethod(name = "approve")
    void approve();

    @UpdateMethod(name = "change_address")
    String changeAddress(String address);

    @UpdateValidatorMethod(updateName = "change_address")
    void checkAddress(String address);

    @QueryMethod(name = "status")
    OrderStatus status();
}
