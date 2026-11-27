#!/usr/bin/env bash
# Run the order demo: the Temporal dev server, four services (one process each) and the web app.
# Usage: ./run.sh python | java | mixed | status | stop <service> | start <service> [python|java] | logs | smoke | down
set -u
cd "$(dirname "$0")"
ROOT=$(pwd)
RUN=$ROOT/.run
SERVICES="orders inventory payments shipping"
URL=http://localhost:5050
mkdir -p "$RUN"

alive() { [ -f "$RUN/$1.pid" ] && kill -0 "$(cat "$RUN/$1.pid")" 2>/dev/null; }
kill_tree() { for child in $(pgrep -P "$1"); do kill_tree "$child"; done; kill "$1" 2>/dev/null; }

launch() {  # name language directory command...
  local name=$1 lang=$2 dir=$3; shift 3
  if alive "$name"; then echo "$name is already running ($(cat "$RUN/$name.lang"))"; return; fi
  (cd "$dir" && exec nohup "$@") < /dev/null > "$RUN/$name.log" 2>&1 &
  echo $! > "$RUN/$name.pid"
  echo "$lang" > "$RUN/$name.lang"
  echo "started $name ($lang)"
}

start_service() {  # service language
  case $2 in
    python) launch "$1" python python uv run python worker.py "$1" ;;
    java) launch "$1" java java mvn -q exec:java@worker "-Dexec.args=$1" ;;
    *) echo "language must be python or java"; exit 1 ;;
  esac
}

start_app() {  # language
  case $1 in
    python) launch app python python uv run python app.py ;;
    java) launch app java java mvn -q exec:java@app ;;
  esac
}

ensure_temporal() {
  temporal operator cluster health > /dev/null 2>&1 && return
  nohup temporal server start-dev > "$RUN/temporal.log" 2>&1 &
  echo $! > "$RUN/temporal.pid"
  for _ in $(seq 1 40); do temporal operator cluster health > /dev/null 2>&1 && { echo "started the Temporal dev server"; return; }; sleep 1; done
  echo "the Temporal dev server did not start, see $RUN/temporal.log"; exit 1
}

wait_ready() {
  printf "waiting for the services"
  for _ in $(seq 1 120); do
    up=$(curl -s "$URL/services" 2> /dev/null | grep -Eo '"up": ?true' | wc -l | tr -d ' ')
    [ "$up" = "4" ] && { echo; return; }
    printf "."; sleep 1
  done
  echo; echo "not every service came up in time, see: ./run.sh logs"
}

banner() {
  echo
  echo "Order demo:      $URL"
  echo "Temporal Web UI: http://localhost:8233"
  echo "Next:            ./run.sh status | ./run.sh stop payments | ./run.sh down   (steps: RUNBOOK.md)"
}

run_all() {  # a language for each service, then the app
  ensure_temporal
  case $1 in python | mixed) command -v uv > /dev/null || { echo "uv is needed"; exit 1; } ;; esac
  case $1 in java | mixed) (cd java && mvn -q compile) || exit 1 ;; esac
  lsof -ti:5050 > /dev/null 2>&1 && ! alive app && { echo "port 5050 is in use by another program"; exit 1; }
  for s in $SERVICES; do
    lang=$1
    if [ "$1" = mixed ]; then if [ "$s" = payments ]; then lang=java; else lang=python; fi; fi
    start_service "$s" "$lang"
  done
  if [ "$1" = java ]; then start_app java; else start_app python; fi
  wait_ready; banner
}

case ${1:-help} in
  python | java | mixed) run_all "$1" ;;
  start)
    [ -n "${2:-}" ] || { echo "usage: ./run.sh start <service> [python|java]"; exit 1; }
    ensure_temporal
    [ "${3:-}" = java ] && (cd java && mvn -q compile)
    start_service "$2" "${3:-python}" ;;
  stop)
    [ -n "${2:-}" ] || { echo "usage: ./run.sh stop <service>"; exit 1; }
    if alive "$2"; then kill_tree "$(cat "$RUN/$2.pid")"; rm -f "$RUN/$2.pid"; echo "stopped $2"; else echo "$2 is not running"; fi ;;
  status)
    for n in $SERVICES app; do
      alive "$n" && echo "$n: running ($(cat "$RUN/$n.lang"))" || echo "$n: stopped"
    done
    temporal operator cluster health > /dev/null 2>&1 && echo "temporal: running" || echo "temporal: stopped"
    echo "$URL" ;;
  logs) tail -n 40 "$RUN"/*.log ;;
  smoke) shift; python3 smoke.py "$@" ;;
  down)
    for n in app $SERVICES; do
      alive "$n" && { kill_tree "$(cat "$RUN/$n.pid")"; echo "stopped $n"; }
      rm -f "$RUN/$n.pid"
    done
    [ -f "$RUN/temporal.pid" ] && { kill_tree "$(cat "$RUN/temporal.pid")"; rm -f "$RUN/temporal.pid"; echo "stopped the Temporal dev server"; } ;;
  *) sed -n '2,3p' "$0" | sed 's/^# //' ;;
esac
