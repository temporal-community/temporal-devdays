from dataclasses import dataclass


@dataclass
class Customer:
    id: str


@dataclass
class Pizza:
    name: str
    price: int


@dataclass
class PizzaOrder:
    customer: Customer
    items: list[Pizza]
    address: str
    is_delivery: bool = True
    prep_seconds: int = 1800


@dataclass
class Bill:
    customer_id: str
    amount: int
