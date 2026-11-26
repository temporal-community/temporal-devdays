import logging
import os

from opentelemetry.sdk.resources import Resource
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor, ConsoleSpanExporter, SimpleSpanProcessor
from temporalio.contrib.opentelemetry import TracingInterceptor
from temporalio.runtime import PrometheusConfig, Runtime, TelemetryConfig


def one_line(span) -> str:
    ms = (span.end_time - span.start_time) / 1e6
    return f"span {span.name:<40} {ms:7.1f} ms  trace={span.context.trace_id:032x}\n"


def setup(service: str, metrics_port: int | None = None, exporter=None) -> TracingInterceptor:
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(name)s: %(message)s")

    if exporter is None and os.getenv("OTEL_EXPORTER_OTLP_ENDPOINT"):
        from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
        processor = BatchSpanProcessor(OTLPSpanExporter())
    else:
        processor = SimpleSpanProcessor(exporter or ConsoleSpanExporter(formatter=one_line))
    provider = TracerProvider(resource=Resource.create({"service.name": service}))
    provider.add_span_processor(processor)

    if metrics_port:
        Runtime.set_default(Runtime(telemetry=TelemetryConfig(
            metrics=PrometheusConfig(bind_address=f"0.0.0.0:{metrics_port}"))))
    return TracingInterceptor(tracer=provider.get_tracer(service))
