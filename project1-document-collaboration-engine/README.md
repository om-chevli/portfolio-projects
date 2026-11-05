# Project 1 — Intelligent Enterprise Document & Collaboration Engine

A Spring Boot service for creating, sharing, searching and summarizing
documents, with per-document access control, plus a React + TypeScript
frontend.

## What it demonstrates

- **Spring Boot REST API** — CRUD + share + search + summary endpoints under `/api/documents`
- **Spring Security** — HTTP Basic with role rules: only `EDITOR` users can create/modify; reads are additionally checked per document in the service layer (owner or shared-with), and non-owners get 404, not 403, so document existence is not leaked
- **MongoDB** — documents persist to MongoDB via Spring Data `MongoTemplate` under the `mongo` profile; the default profile uses an in-memory repository with the same interface, so the app runs with zero setup
- **AI feature (honest scope)** — `SummaryService` is an interface. The shipped implementation is an offline *extractive* summarizer (sentence scoring by word frequency). An LLM-backed implementation (e.g. LangChain4j → AWS Bedrock) plugs into the same interface; that integration is a documented seam, not a shipped feature
- **React + TypeScript frontend** — `frontend/` (Vite): list/create documents and fetch summaries through a typed API client
- **Tests** — JUnit service tests (access control, sharing rules, versioning, search isolation, summarizer) and MockMvc controller tests (401/403/201 paths)

## Run

```bash
mvn -pl project1-document-collaboration-engine spring-boot:run   # from repo root
# or, inside this folder:
mvn spring-boot:run
```

Then:

```bash
curl -u om:om-password localhost:8081/api/documents \
  -H 'Content-Type: application/json' \
  -d '{"title":"Design notes","content":"WebSockets push changes. Clients apply them in order. Push keeps every screen in sync."}'
curl -u om:om-password localhost:8081/api/documents          # list
curl -u om:om-password localhost:8081/api/documents/<id>/summary
```

Demo users: `om` / `om-password` (EDITOR), `guest` / `guest-password` (VIEWER).

With MongoDB instead of the in-memory store:

```bash
docker compose up -d mongo        # from repo root
mvn spring-boot:run -Dspring-boot.run.profiles=mongo
```

Frontend:

```bash
cd frontend && npm install && npm run dev
```

## Design notes

- Repository interface first (`DocumentRepository`), two implementations —
  swapping storage never touches the service or controller.
- Version counter on every update: the basis for optimistic locking /
  conflict handling if real-time co-editing is added.
- Search only ever returns documents the caller can access (tested).
