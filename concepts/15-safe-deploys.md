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
