#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
python3 scripts/verify_repo.py
(cd demos/observability/python && uv run pytest -q)
python3 scripts/behaviour.py "$@"
(cd demos/order-management/python && uv run pytest -q)
