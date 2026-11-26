from dataclasses import dataclass


@dataclass
class OrderInput:
    order_id: str
    address: str
    scenario: str = "happy"  # happy | approval | bad_card | flaky_card | ship_fails


@dataclass
class OrderOutput:
    status: str
    tracking_id: str
    address: str


@dataclass
class OrderStatus:
    step: str
    address: str
