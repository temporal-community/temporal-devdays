#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
python3 scripts/verify_repo.py
python3 scripts/behaviour.py "$@"
(cd demos/order-management/python && uv run pytest -q)
(cd demos/order-management/java && mvn -q test)
