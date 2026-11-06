# Portfolio Projects — Om Chevli

Three backend projects built to demonstrate the stack in Senior Java
Developer posting: Spring Boot, Spring Security, REST API design, MongoDB,
WebSockets, Redis, JUnit/Mockito, and React + TypeScript clients.

| # | Project | Stack | Port |
|---|---------|-------|------|
| 1 | [Intelligent Document & Collaboration Engine](project1-document-collaboration-engine/) | Spring Boot, Spring Security, MongoDB (profile), React + TypeScript | 8081 |
| 2 | [Public & Internal REST API Platform](project2-api-platform/) | Spring Boot, OpenAPI/Swagger, JUnit + Mockito, TypeScript client | 8082 |
| 3 | [Multi-Tenant Notification Service](project3-notification-service/) | Spring Boot, STOMP WebSockets, Redis pub/sub (profile), TypeScript client | 8083 |

## Build & test everything

Requires Java 21 and Maven 3.9+.

```bash
mvn verify
```

Verified 2026-11-04: `mvn test` — **24 tests, 0 failures** across the three modules (plus TypeScript typechecks for both frontends).

Every project also runs standalone with **no external services**: databases
and message brokers sit behind interfaces with in-memory implementations as
the default, and the real ones (MongoDB, Redis) activate with a Spring
profile — see each project's README and the root `docker-compose.yml`.

## Honesty note

These are real, runnable portfolio implementations built in November 2026.
They implement the *features* described in each README at portfolio scale
(single service, in-memory defaults). They are not production deployments,
and no usage metrics are claimed for them. Where a feature is a seam for a
larger system (e.g. the LLM summarizer interface in Project 1), the README
says so explicitly.
