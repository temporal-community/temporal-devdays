"""Behaviour checks: solutions pass, exercises fail on a TODO, overlay passes.
With --live also runs the Worker and starter of each solution (needs the dev server).
Standard library only. Exit code 1 if any row fails."""
import json
import shlex
import shutil
import subprocess
import sys
import tempfile
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LANGS = ("python", "java")
rows: list[tuple[str, str, str]] = []


def test_cmd(lang: str, d: Path) -> list[str]:
    if lang == "python":
        return ["uv", "run", "--project", str(ROOT / "python"), "pytest", "-q", "-x",
                "-c", str(ROOT / "python" / "pyproject.toml"), "--rootdir", str(d), str(d)]
    return ["mvn", "-q", "-f", str(d / "pom.xml"), "test"]


def run(lang: str, d: Path) -> tuple[int, str]:
    p = subprocess.run(test_cmd(lang, d), capture_output=True, text=True, cwd=d)
    return p.returncode, p.stdout + p.stderr


def record(name: str, ok: bool, note: str = "") -> None:
    rows.append((name, "ok" if ok else "FAIL", note))


def pairs(lang: str):
    for topic in sorted((ROOT / lang).glob("[0-9][0-9]_*")):
        ex, so = topic / "exercise", topic / "solution"
        if ex.is_dir() and so.is_dir():
            yield topic.name, ex, so


def ignore(_dir, names):
    return [n for n in names if n in {".venv", "target", "__pycache__", ".pytest_cache"}]


def behaviour() -> None:
    for lang in LANGS:
        for topic, ex, so in pairs(lang):
            code, _ = run(lang, so)
            record(f"{lang}/{topic}: solution passes", code == 0)
            code, out = run(lang, ex)
            broken = any(w in out for w in ("SyntaxError", "COMPILATION ERROR", "ImportError", "ModuleNotFoundError"))
            failed_on_todo = code != 0 and "TODO" in out and not broken
            record(f"{lang}/{topic}: exercise fails on a TODO", failed_on_todo,
                   "" if failed_on_todo else ("passed unexpectedly" if code == 0 else ("failed on a syntax, compile or import error" if broken else "failed, but not on a TODO")))
            with tempfile.TemporaryDirectory() as tmp:
                work = Path(tmp) / ex.name
                shutil.copytree(ex, work, ignore=ignore)
                for item in so.iterdir():
                    if item.name in {".venv", "target", "__pycache__", ".pytest_cache"}:
                        continue
                    dest = work / item.name
                    if dest.exists():
                        shutil.rmtree(dest) if dest.is_dir() else dest.unlink()
                    shutil.copytree(item, dest, ignore=ignore) if item.is_dir() else shutil.copy2(item, dest)
                code, _ = run(lang, work)
            record(f"{lang}/{topic}: overlay passes", code == 0)


def live() -> None:
    table = json.loads((ROOT / "scripts" / "live.json").read_text())
    for item in table:
        d = ROOT / item["dir"]
        started = []
        for cmd in item.get("before", []):
            subprocess.run(shlex.split(cmd), capture_output=True)
        try:
            for cmd in item["workers"]:
                started.append(subprocess.Popen(shlex.split(cmd), cwd=d, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL))
            time.sleep(item.get("wait", 6))
            p = subprocess.run(shlex.split(item["starter"]), cwd=d, capture_output=True, text=True, timeout=180)
            ok = item["expect"] in p.stdout
            record(f"live {item['dir']}", ok, "" if ok else f"expected '{item['expect']}'")
        except Exception as err:  # a failed live run must not stop the other rows
            record(f"live {item['dir']}", False, str(err)[:80])
        finally:
            for proc in started:
                proc.terminate()
            for proc in started:
                try:
                    proc.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    proc.kill()
            for cmd in item.get("cleanup", []):
                subprocess.run(shlex.split(cmd), capture_output=True)


def main() -> int:
    behaviour()
    if "--live" in sys.argv:
        live()
    width = max((len(r[0]) for r in rows), default=10)
    print(f"behaviour: {len(rows)} check(s)")
    for name, status, note in rows:
        print(f"  {status:4} {name:<{width}} {note}")
    bad = [r for r in rows if r[1] != "ok"]
    print("behaviour: " + ("FAILED" if bad else "OK"))
    return 1 if bad else 0


if __name__ == "__main__":
    sys.exit(main())
