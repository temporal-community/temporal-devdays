from dataclasses import dataclass


@dataclass
class Claim:
    amount: int
    kind: str
