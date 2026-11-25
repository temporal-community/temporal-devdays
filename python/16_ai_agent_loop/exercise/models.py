from dataclasses import dataclass


@dataclass
class Message:
    role: str
    content: str


@dataclass
class Decision:
    tool: str = ""
    argument: str = ""
    answer: str = ""
