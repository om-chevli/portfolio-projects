# Draft resume bullets — Projects section (to finalize after `mvn test` passes)

Placement: new "Projects" section on the honest resume, after Professional
Experience, before Education. Skills line additions (only what the code uses):
WebSockets, Redis, TypeScript, MongoDB (already listed), JUnit/Mockito
(JUnit already listed).

## Projects

**Intelligent Document & Collaboration Engine** — Spring Boot, Spring Security, MongoDB, React, TypeScript
- Built a document service with create/share/search and per-document access control (owner or shared-with), with Spring Security role rules for writes and 404-instead-of-403 reads so document existence is not leaked.
- Added a document summarization feature behind a `SummaryService` interface (offline extractive implementation shipped; LLM provider can be swapped in) and a React + TypeScript UI; MongoDB persistence via Spring Data behind a repository interface (in-memory default).

**Public & Internal REST API Platform** — Spring Boot, OpenAPI, JUnit, Mockito
- Built one service serving two audiences: a versioned, rate-limited public API returning DTOs that exclude internal fields, and an internal API guarded by an API-key filter returning full records.
- Wrote JUnit/Mockito unit tests and MockMvc end-to-end tests, including a test proving internal cost data never appears in public responses; documented the API with OpenAPI/Swagger.

**Multi-Tenant Notification Service** — Spring Boot, WebSockets (STOMP), Redis, React, TypeScript
- Built a notification service with tenant isolation enforced in the store, the pub/sub bus, and per-tenant WebSocket topics, so no tenant can read or receive another tenant's notifications.
- Delivered real-time updates to a TypeScript client over STOMP/WebSocket (no polling); event bus is in-memory by default with a Redis pub/sub implementation behind a Spring profile for multi-instance fan-out.

Notes kept OFF the resume (do not add):
- No "40% test coverage acceleration", no "30% latency reduction", no
  "team champion / cross-functional team" claims — none of that happened.
- "Personal portfolio projects, Nov 2026" framing is the honest label.
- Test counts go in only after the verified run (fill in: 24 tests passing (9 Project 1, 9 Project 2, 6 Project 3), verified 2026-11-04 with `mvn test` — BUILD SUCCESS).
