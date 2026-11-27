.PHONY: setup check check-live check-links check-todos

setup:
	cd python && uv sync

check:
	./scripts/check.sh

check-live:
	./scripts/check.sh --live

check-links:
	python3 scripts/check_links.py

check-todos:
	python3 scripts/behaviour.py --per-todo
