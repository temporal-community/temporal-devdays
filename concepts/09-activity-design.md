# 09: Activity design

## The problem
A Workflow that works can still be built badly. Two common habits cause trouble: putting simple decisions into Activities, and putting several outside calls into one Activity.

## The idea
Use an **Activity** only for things that touch the outside world and can fail: a database, an email, an API. Plain decisions (an `if` on the data you already have) belong in the Workflow, because an Activity costs time, history and a worker round trip. Give each outside call its own Activity, so a retry repeats only the step that failed.

## Picture
```
Bad :  create_user = [ save to database + send email ]   retry → saved twice
Good:  add_user ──▶ send_welcome                          retry → only the email
```

## Use it when
- A step calls something outside your code (Activity).
- Two outside calls could fail on their own (two Activities).

## Don't use it when
- The step is pure logic on data you already hold. Keep it in the Workflow.

## Common mistake
Wrapping every function in an Activity "to be safe". It adds cost and hides simple logic. The other direction is just as common: one Activity that writes to a database and then sends an email, so a failed email retry writes the record twice.

## Learn more
- [Activities](https://docs.temporal.io/activities)
- [Activity definition](https://docs.temporal.io/activity-definition)
- [Retry Policies](https://docs.temporal.io/encyclopedia/retry-policies)
- [Idempotency and durable execution](https://temporal.io/blog/idempotency-and-durable-execution)
