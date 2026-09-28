# API examples

Create a subscription:

```bash
curl -X POST http://localhost:8080/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{"customerId":"11111111-1111-1111-1111-111111111111","plan":"MONTHLY"}'
```

Get it:

```bash
curl http://localhost:8080/api/subscriptions/11111111-1111-1111-1111-111111111111
```

Change plan:

```bash
curl -X PUT http://localhost:8080/api/subscriptions/11111111-1111-1111-1111-111111111111/plan \
  -H "Content-Type: application/json" \
  -d '{"plan":"ANNUAL"}'
```
