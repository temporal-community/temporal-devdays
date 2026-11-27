# 15: Safe deploys

## The problem
You add a new step to a Workflow. Some Workflows are already running, and their history has no such step. When a Worker replays an old Workflow with the new code, the steps no longer match and the Workflow gets stuck.

## The idea
**Patching** lets old and new live together. You wrap the new step in a check (`patched` in Python, `getVersion` in Java). A Workflow that started before the change takes the old path and replays fine. A Workflow that starts after the change takes the new path. Once no old runs are left, you remove the check. **Worker Versioning** is the other approach: you deploy the new code as a new version and let old runs finish on the old version.

## Picture
```
old history:  take_order → bake
new code:     take_order → [if patched: check_stock] → bake
old run replays: skips check_stock   |   new run: runs check_stock
```

## How it works
- **Replay runs new code against old history.** A change that alters the commands (add, remove, reorder a step) makes old runs fail with a non-determinism error.
- **Patching**: `patched("id")` in Python and `Workflow.getVersion` in Java mark the point of the change in the history. Old runs take the old branch. New runs record the marker and take the new branch.
- When no open Workflow could be on the old branch any more, you deprecate the patch and later remove the old code.
- **Worker Versioning**: Workers declare a build version, and Temporal keeps Workflows on the version they started with (pinned) or moves them to newer ones (auto-upgrade). You keep the old Workers running until their pinned Workflows finish.
- Safe edits need no patch: Activity code, logging, and changes that do not alter the commands.

## Production use cases
- **Adding a step to a long-running process**: add a fraud check to an order flow that has Workflows open for weeks.
- **Changing a timeout or a branch** in a flow with open approvals.
- **Rolling out a risky change gradually** by routing new Workflows to a new version.

## Design decisions
- **Patch or Worker Versioning?** Patching is code-level and needs no extra infrastructure. Worker Versioning avoids patches in code but means running more than one version of Workers. Pick one per team and stay consistent.
- **Replay in CI** with histories exported from production (concept 05). It is the best protection.
- **Short-lived Workflows** that finish in seconds can be handled with a normal deploy, because nothing old is open.
- **Clean up**: track every patch and remove it once the old runs are gone.
- **Roll back**: a Worker can be rolled back to old code as long as history it has already written still matches.

## Failure modes
- **Unpatched change**: blocked Workflows after deploy. They continue when the code is fixed or reverted.
- **Removing a patch too early**: a still-open old run now fails replay.
- **Forgetting the old Worker version** (Worker Versioning): pinned Workflows wait for a Worker that is gone.
- **Testing only with fresh Workflows**: they never take the old path.

## What to measure
- Workflow task failures per type after each deploy.
- Number of open Workflows per version (patch markers or Worker version).
- Replay results in CI.

## Use it when
- You change the steps of a Workflow that may have open runs.
- You can keep a recorded history and replay it in a test before you deploy.

## Don't use it when
- No Workflow is ever open for long. If every run finishes within seconds, a plain deploy is enough.

## Common mistake
Adding, removing or reordering a step with no patch and no replay test. Everything passes locally, and the first old Workflow that wakes up in production fails. Replay a recorded history in your tests.

## Learn more
- [Python: versioning](https://docs.temporal.io/develop/python/versioning)
- [Java: versioning](https://docs.temporal.io/develop/java/versioning)
- [Worker Versioning](https://docs.temporal.io/production-deployment/worker-deployments/worker-versioning)
- [Workflow definition and determinism](https://docs.temporal.io/workflow-definition)
