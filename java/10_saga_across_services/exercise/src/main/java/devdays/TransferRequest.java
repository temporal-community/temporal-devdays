package devdays;

public record TransferRequest(String fromAccount, String toAccount, int amount, String failAt) {}
