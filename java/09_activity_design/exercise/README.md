# Exercise 09: Activity design

Fix two design mistakes: logic that does not need an Activity, and one Activity that calls two services.

[Concept](../../../concepts/09-activity-design.md) · Slides: `wva-example, wva-mistakes, wva-split`

The code in this exercise runs, but it is badly designed. The check explains why and names the TODO.

## Run the check
```bash
cd java/09_activity_design/exercise
mvn test
```
The check fails until the TODOs are done.

## Tasks
- [ ] TODO 1 (`ClaimWorkflowImpl.java`): decide in the Workflow with plain code, do not call `evaluateClaim`
- [ ] TODO 2 (`OnboardWorkflowImpl.java`): call `addUser` and then `sendWelcome` instead of `createUser`

## The check
The Claim decision runs no Activity. The new user is added once even though the email fails on the first try.

## Stretch
Make `send_welcome` fail twice and confirm the user is still added only once.

Solution: [solution](../solution)
