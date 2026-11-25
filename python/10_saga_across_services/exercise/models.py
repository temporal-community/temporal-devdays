from dataclasses import dataclass


@dataclass
class TransferRequest:
    from_account: str
    to_account: str
    amount: int
    fail_at: str = ""
