"""Check that every http(s) link in the Markdown files still works.
Standard library plus curl. Needs the network, so CI runs it nightly, not on pull requests.
Exit code 1 if any link fails."""
import re
import sys
import subprocess
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SKIP_DIRS = {".git", ".venv", "target", "node_modules", "__pycache__"}
LINK = re.compile(r"\]\((https?://[^)\s]+)\)|<(https?://[^>\s]+)>")


def links() -> dict[str, list[str]]:
    found: dict[str, list[str]] = {}
    for md in ROOT.rglob("*.md"):
        if set(md.relative_to(ROOT).parts) & SKIP_DIRS:
            continue
        for a, b in LINK.findall(md.read_text()):
            found.setdefault(a or b, []).append(str(md.relative_to(ROOT)))
    return found


def status(url: str) -> str:
    if "localhost" in url or "127.0.0.1" in url:
        return "skipped"
    p = subprocess.run(
        ["curl", "-sL", "-o", "/dev/null", "-w", "%{http_code}", "--max-time", "20", "--retry", "2", url],
        capture_output=True, text=True)
    code = p.stdout.strip()
    return "ok" if code.isdigit() and 200 <= int(code) < 400 else f"HTTP {code or 'no answer'}"


def main() -> int:
    found = links()
    with ThreadPoolExecutor(8) as pool:
        results = dict(zip(found, pool.map(status, found)))
    bad = {u: s for u, s in results.items() if s not in ("ok", "skipped")}
    for u, s in sorted(bad.items()):
        print(f"  FAIL {s}: {u}  (in {', '.join(sorted(set(found[u])))})")
    print(f"links: {len(found)} checked, {len(bad)} failed")
    return 1 if bad else 0


if __name__ == "__main__":
    sys.exit(main())
