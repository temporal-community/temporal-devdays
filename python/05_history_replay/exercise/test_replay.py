from pathlib import Path

import pytest

from replay import event_types, replay_history
from swapped_workflow import SwappedPizzaWorkflow

HISTORY = (Path(__file__).parent / "history.json").read_text()


def test_reads_the_history():
    types = event_types(HISTORY)
    assert types[0] == "WORKFLOW_EXECUTION_STARTED"
    assert "TIMER_STARTED" in types
    assert types[-1] == "WORKFLOW_EXECUTION_COMPLETED"


async def test_the_same_code_replays():
    await replay_history(HISTORY)


async def test_changed_code_fails_to_replay():
    with pytest.raises(Exception):
        await replay_history(HISTORY, SwappedPizzaWorkflow)
