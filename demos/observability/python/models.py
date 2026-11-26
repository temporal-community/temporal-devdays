from dataclasses import dataclass


@dataclass
class PizzaOrder:
    order_id: str
    pizza: str
    prep_seconds: float = 2
