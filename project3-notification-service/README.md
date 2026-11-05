# Project 3 — AI-Augmented Workflow & Multi-Tenant Notification Service

A Spring Boot service that delivers notifications to users in real time,
isolated per tenant, with both a REST API and a STOMP/WebSocket live channel.

## How it works

1. Something happens in a workflow — a REST call to `POST /api/notifications`,
   or a STOMP message to `/app/workflow-events`.
2. The notification is stored **under its tenant** (`X-Tenant-Id` header;
   tenant is part of the caller's identity, never a query parameter — there
   is no API shape that can read another tenant's data).
3. It is published on an **event bus**: in-memory by default, or **Redis
   pub/sub** (one channel per tenant, `notifications:{tenantId}`) under the
   `redis` profile, so multiple instances fan out the same event.
4. The `LiveBroadcaster` forwards it to the tenant's WebSocket topic
   `/topic/tenant-{tenantId}`; subscribed React/TypeScript clients update
   instantly — the real-time sync between backend state and UI screens.

## What it demonstrates

- **Multi-tenancy enforced at every layer** — store keyed by tenant, bus
  subscriptions per tenant, WebSocket topics per tenant; tests prove tenant
  A cannot read, mark-read, or receive tenant B's notifications
- **WebSockets (STOMP)** with a typed TypeScript client (`frontend/`)
  using `@stomp/stompjs`
- **Redis** as a profile-swappable pub/sub backend behind an `EventBus`
  interface (Jackson JSON payloads)
- **AI-assisted development, truthfully stated** — this codebase was built
  with AI pair-programming assistance (Muse) and is structured for it:
  small interfaces, seams for implementations, and a test suite that lets a
  developer (human or AI) verify changes quickly. No productivity percentage
  is claimed — the verifiable artefact is the code and its tests

## Run

```bash
mvn -pl project3-notification-service spring-boot:run   # from repo root
```

```bash
# create a notification for tenant "acme"
curl -X POST localhost:8083/api/notifications -H 'X-Tenant-Id: acme' \
  -H 'Content-Type: application/json' \
  -d '{"userId":"u1","type":"TASK","message":"Deploy finished"}'

# read it back — only as acme
curl 'localhost:8083/api/notifications?userId=u1' -H 'X-Tenant-Id: acme'
curl 'localhost:8083/api/notifications?userId=u1' -H 'X-Tenant-Id: globex'   # → []
```

With Redis (multi-instance fan-out):

```bash
docker compose up -d redis        # from repo root
mvn spring-boot:run -Dspring-boot.run.profiles=redis
```

TypeScript client: `frontend/src/notificationClient.ts` — backlog fetch,
send, and live subscription in ~80 lines.
