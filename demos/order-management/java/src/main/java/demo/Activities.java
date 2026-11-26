package demo;

import demo.Models.OrderInput;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;

@ActivityInterface
public interface Activities {
    @ActivityMethod(name = "check_stock")
    List<String> checkStock(OrderInput order);

    @ActivityMethod(name = "reserve_stock")
    void reserveStock(OrderInput order);

    @ActivityMethod(name = "release_stock")
    void releaseStock(OrderInput order);

    @ActivityMethod(name = "charge")
    void charge(OrderInput order);

    @ActivityMethod(name = "ship_item")
    void shipItem(String orderId, String item);
}
