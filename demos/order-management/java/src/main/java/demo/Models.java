package demo;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Models {
    public record OrderInput(@JsonProperty("order_id") String orderId, String address, String scenario) {}

    public record OrderOutput(String status, @JsonProperty("tracking_id") String trackingId, String address) {}

    public record OrderStatus(String step, String address) {}
}
