# Project 2 — High-Scale Public & Internal REST API Platform

One Spring Boot service that serves two audiences safely:

- **Public API** (`/api/public/v1/**`) — versioned, rate-limited, and returns
  public DTOs only. Internal fields (`internalCost`) cannot leak because the
  public controller physically cannot see them: it maps every record through
  `PublicItemDto`.
- **Internal API** (`/api/internal/v1/**`) — full records and write access,
  guarded by an `X-Api-Key` filter.

This is the pattern behind APIs consumed simultaneously by web apps, mobile
apps and embedded clients such as an Office add-in task pane — all of them
are HTTPS+JSON consumers of the same public surface (`clients/apiClient.ts`
is a shared TypeScript client usable from any of them).

## What it demonstrates

- **API versioning** in the URL (`/v1/`) and DTO separation per audience
- **Servlet filters** — API-key auth for internal routes; fixed-window
  per-IP rate limiting (429 + JSON error) for public routes
- **OpenAPI docs** — springdoc: Swagger UI at `/swagger-ui.html`, spec at `/v3/api-docs`
- **JUnit + Mockito** — service tests with a mocked repository (category
  filtering, 404 handling, price validation) and MockMvc end-to-end tests
  proving the internal-cost field never appears in public responses
- **CI** — the repo-level GitHub Actions workflow builds and tests all three
  projects on every push (`.github/workflows/build.yml`)

## Run

```bash
mvn -pl project2-api-platform spring-boot:run   # from repo root
```

```bash
curl localhost:8082/api/public/v1/items                      # public list (no internalCost)
curl localhost:8082/api/internal/v1/items                    # 401 without a key
curl -H 'X-Api-Key: dev-internal-key' localhost:8082/api/internal/v1/items/kb-001
```

The dev API key defaults to `dev-internal-key`; set `INTERNAL_API_KEY` in the
environment for anything beyond a local demo. Public rate limit defaults to
100 requests/minute/IP (`PUBLIC_RATE_LIMIT`).
