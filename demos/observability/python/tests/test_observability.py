import asyncio
import logging
import uuid

import pytest
import telemetry
from activities import bake, deliver, take_order
from models import PizzaOrder
from opentelemetry.sdk.trace.export.in_memory_span_exporter import InMemorySpanExporter
from temporalio.testing import WorkflowEnvironment
from temporalio.worker import Worker
from workflows import PizzaWorkflow


@pytest.fixture
async def run():
    exporter = InMemorySpanExporter()
    tracing = telemetry.setup("test", exporter=exporter)
    async with await WorkflowEnvironment.start_local(
        interceptors=[tracing], dev_server_extra_args=["--search-attribute", "Step=Keyword"]
    ) as env:
        async with Worker(env.client, task_queue="pizza", workflows=[PizzaWorkflow],
                          activities=[take_order, bake, deliver]):
            async def go() -> str:
                return await asyncio.wait_for(env.client.execute_workflow(
                    PizzaWorkflow.run, PizzaOrder("1", "margherita", prep_seconds=0.1),
                    id=f"pizza-{uuid.uuid4().hex[:6]}", task_queue="pizza"), 30)
            yield go, exporter


async def test_workflow_runs_and_traces_every_step(run):
    go, exporter = run
    assert await go() == "margherita delivered"
    names = {s.name for s in exporter.get_finished_spans()}
    for expected in ("StartWorkflow:PizzaWorkflow", "RunWorkflow:PizzaWorkflow",
                     "StartActivity:take_order", "RunActivity:bake", "RunActivity:deliver"):
        assert expected in names, f"missing span {expected}"


async def test_all_spans_share_one_trace(run):
    go, exporter = run
    await go()
    assert len({s.context.trace_id for s in exporter.get_finished_spans()}) == 1


async def test_workflow_logs_once(run, caplog):
    go, _ = run
    with caplog.at_level(logging.INFO):
        await go()
    started = [r for r in caplog.records if "order started" in r.getMessage()]
    assert len(started) == 1
