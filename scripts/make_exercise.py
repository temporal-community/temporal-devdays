"""Build an exercise folder from a solution folder.

Usage: python3 scripts/make_exercise.py python/solution_01_hello

Markers in the solution:
    # TODO 1 start: hint            (Python)   // TODO 1 start: hint   (Java)
    ...solution lines...
    # TODO 1 end
    Add [workflow] after "start" for code that runs inside a Workflow:
    # TODO 1 start [workflow]: hint
The lines between the markers are replaced by a stub that names the TODO.
README.md is not copied: write the exercise README by hand.
"""
import re
import shutil
import sys
from pathlib import Path

START = re.compile(r"^(\s*)(#|//) TODO (\d+) start(?: \[(workflow)\])?: (.*)$")
SKIP = {".venv", "target", "__pycache__", ".pytest_cache", "README.md"}
PY_IMPORT = "from temporalio.exceptions import ApplicationError"


def stub(indent: str, lang: str, workflow: bool, n: str, hint: str, tail: bool = True) -> str:
    hint = hint.replace("\\", "\\\\").replace('"', '\\"')
    if lang == "py":
        if workflow:
            return f'{indent}raise ApplicationError("TODO {n}: {hint}", non_retryable=True)'
        return f'{indent}raise NotImplementedError("TODO {n}: {hint}")'
    # Java: a throw in the middle of a method makes the next line unreachable, so use if (true)
    lead = indent + ("" if tail else "if (true) ")
    if workflow:
        return (f'{lead}throw io.temporal.failure.ApplicationFailure'
                f'.newNonRetryableFailure("TODO {n}: {hint}", "Todo");')
    return f'{lead}throw new UnsupportedOperationException("TODO {n}: {hint}");'


def convert(text: str, lang: str) -> str:
    out, lines, i, needs_import = [], text.split("\n"), 0, False
    while i < len(lines):
        m = START.match(lines[i])
        if not m:
            out.append(lines[i])
            i += 1
            continue
        indent, comment, n, wf, hint = m.groups()
        end = f"{comment} TODO {n} end"
        j = i + 1
        while j < len(lines) and lines[j].strip() != end:
            j += 1
        if j == len(lines):
            sys.exit(f"missing '{end}'")
        rest = next((l.strip() for l in lines[j + 1:] if l.strip()), "")
        tail = rest.startswith("}") or lang == "py"
        out += [lines[i], stub(indent, lang, bool(wf), n, hint, tail), lines[j]]
        needs_import |= bool(wf) and lang == "py"
        i = j + 1
    if needs_import and PY_IMPORT not in text:
        k = next((x for x, l in enumerate(out) if l.startswith(("from ", "import "))), 0)
        out.insert(k, PY_IMPORT)
    return "\n".join(out)


def main() -> None:
    src = Path(sys.argv[1]).resolve()
    if not src.name.startswith("solution_"):
        sys.exit("expected a solution_* folder")
    dst = src.with_name(src.name.replace("solution_", "exercise_", 1))
    for item in src.rglob("*"):
        rel = item.relative_to(src)
        if set(rel.parts) & SKIP or item.is_dir():
            continue
        target = dst / rel
        target.parent.mkdir(parents=True, exist_ok=True)
        if item.suffix in (".py", ".java"):
            target.write_text(convert(item.read_text(), "py" if item.suffix == ".py" else "java"))
        else:
            shutil.copy2(item, target)
    print(f"wrote {dst}")


if __name__ == "__main__":
    main()
