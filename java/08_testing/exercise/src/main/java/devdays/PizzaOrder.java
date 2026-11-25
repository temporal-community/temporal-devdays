package devdays;

import java.util.List;

public record PizzaOrder(
        Customer customer, List<Pizza> items, String address, boolean delivery, long prepSeconds) {}
