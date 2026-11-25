"""Structure checks. Standard library only. Exit code 1 if any check fails."""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LANGS = ("python", "java")
SECTIONS = ["## The problem", "## The idea", "## Picture", "## Use it when",
            "## Don't use it when", "## Common mistake", "## Learn more"]
BANNED = [r"/Users/", r"[\w.+-]+@(gmail|outlook|yahoo|hotmail)\.com", "chu" + "bb"]
SKIP_DIRS = {".git", ".venv", "target", "__pycache__", ".pytest_cache", "node_modules"}

errors: list[str] = []


def fail(msg: str) -> None:
    errors.append(msg)


def walk(pattern: str):
    for p in ROOT.rglob(pattern):
        if not (set(p.relative_to(ROOT).parts) & SKIP_DIRS):
            yield p


def exercise_dirs(lang: str, prefix: str) -> dict[str, Path]:
    base = ROOT / lang
    return {d.name[len(prefix):]: d for d in sorted(base.glob(prefix + "[0-9][0-9]_*")) if d.is_dir()}


def check_pairs() -> int:
    names = {}
    total = 0
    for lang in LANGS:
        ex = exercise_dirs(lang, "exercise_")
        so = exercise_dirs(lang, "solution_")
        names[lang] = set(ex)
        total += len(ex)
        for n in ex.keys() - so.keys():
            fail(f"{lang}/exercise_{n} has no solution_{n}")
        for n in so.keys() - ex.keys():
            fail(f"{lang}/solution_{n} has no exercise_{n}")
    if names["python"] != names["java"]:
        diff = names["python"] ^ names["java"]
        fail(f"python and java exercises differ: {sorted(diff)}")
    return total


def has_tests(d: Path, lang: str) -> bool:
    if lang == "python":
        return any(d.glob("test_*.py")) or any(d.glob("tests/test_*.py"))
    return any(d.glob("src/test/java/**/*Test.java"))


def check_exercise_folders() -> None:
    for lang in LANGS:
        for prefix in ("exercise_", "solution_"):
            for name, d in exercise_dirs(lang, prefix).items():
                rel = f"{lang}/{prefix}{name}"
                readme = d / "README.md"
                if not readme.exists():
                    fail(f"{rel} has no README.md")
                    continue
                text = readme.read_text()
                nn = name[:2]
                concepts = list((ROOT / "concepts").glob(f"{nn}-*.md"))
                if not concepts:
                    fail(f"{rel}: no concepts/{nn}-*.md")
                elif not any(c.name in text for c in concepts):
                    fail(f"{rel}/README.md does not link to concepts/{concepts[0].name}")
                if not has_tests(d, lang):
                    fail(f"{rel} has no tests")
                if prefix == "exercise_":
                    sources = [p for p in d.rglob("*") if p.suffix in (".py", ".java") and not (set(p.relative_to(d).parts) & SKIP_DIRS)]
                    body = "\n".join(p.read_text() for p in sources)
                    todos = sorted(set(re.findall(r"TODO (\d+) start", body)))
                    if not todos:
                        fail(f"{rel} has no 'TODO N start' markers")
                    for t in todos:
                        if f"TODO {t}" not in text:
                            fail(f"{rel}/README.md does not list TODO {t}")


def check_concepts() -> None:
    for f in sorted((ROOT / "concepts").glob("[0-9][0-9]-*.md")):
        text = f.read_text()
        for s in SECTIONS:
            if s not in text:
                fail(f"{f.relative_to(ROOT)} is missing '{s}'")


def check_links() -> None:
    link = re.compile(r"\[[^\]]*\]\(([^)\s]+)\)")
    for md in walk("*.md"):
        for target in link.findall(md.read_text()):
            if target.startswith(("http://", "https://", "mailto:", "#")):
                continue
            path = target.split("#")[0]
            if path and not (md.parent / path).resolve().exists():
                fail(f"{md.relative_to(ROOT)}: broken link {target}")


def check_root_table(total: int) -> None:
    text = (ROOT / "README.md").read_text()
    for lang in LANGS:
        for name in exercise_dirs(lang, "exercise_"):
            if f"{lang}/exercise_{name}" not in text or f"{lang}/solution_{name}" not in text:
                fail(f"README.md table is missing {lang}/exercise_{name} or its solution")


def check_banned() -> None:
    pats = [re.compile(p, re.I) for p in BANNED]
    for f in walk("*"):
        if f.is_file() and f.suffix in {".md", ".py", ".java", ".xml", ".yml", ".yaml", ".json", ".toml", ".sh", ""}:
            if f.name in {"verify_repo.py"}:
                continue
            try:
                text = f.read_text()
            except (UnicodeDecodeError, OSError):
                continue
            for p in pats:
                if p.search(text):
                    fail(f"{f.relative_to(ROOT)}: matches banned pattern {p.pattern}")


def check_cross_imports() -> None:
    for f in walk("*.py"):
        if re.search(r"^\s*(from|import)\s+(shared|common)\b", f.read_text(), re.M):
            fail(f"{f.relative_to(ROOT)}: imports a shared package")


def main() -> int:
    total = check_pairs()
    check_exercise_folders()
    check_concepts()
    check_links()
    check_root_table(total)
    check_banned()
    check_cross_imports()
    print(f"structure: {total} exercise folder(s) checked across python and java")
    for e in errors:
        print(f"  FAIL {e}")
    print("structure: " + ("FAILED" if errors else "OK"))
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
