# class-work-web-wk3 — Multi-Container App (HW2)

Spring Boot backend + PostgreSQL + Redis, run together with Docker Compose.

## Services

- **app** — Spring Boot backend (built from the local `Dockerfile`), port `8080`.
- **db** — PostgreSQL 16, port `5432`, data persisted in the named volume `pgdata`.
- **redis** — Redis 7, port `6379` (kept from the earlier assignment, optional for HW2).

## Setup

1. Copy the example env file and fill in real values:
   ```bash
   cp .env.example .env
   ```
   Edit `.env` and set a real `DB_PASSWORD`. `.env` is gitignored — never commit it.

2. Start everything:
   ```bash
   docker compose up --build
   ```

3. The app is now available at `http://localhost:8080`.

## Endpoints

- `GET /health` — simple liveness check.
- `GET /actuator/health` — Spring Boot Actuator health check.
- `GET /api/items` — list all items (reads from PostgreSQL).
- `GET /api/items/{id}` — fetch one item.
- `POST /api/items` — create an item, e.g.:
  ```bash
  curl -X POST http://localhost:8080/api/items \
    -H "Content-Type: application/json" \
    -d '{"name":"first item","description":"hello db"}'
  ```
- `DELETE /api/items/{id}` — delete an item.
- `POST /redis/set?key=foo&value=bar` / `GET /redis/get?key=foo` — Redis read/write (from HW1).

## Verifying persistence

```bash
docker compose down        # stop and remove containers, volume stays
docker compose up          # start again, no --build needed
curl http://localhost:8080/api/items   # previously created items are still there
```

Data only disappears if you explicitly run `docker compose down -v`.

## Notes on secrets

- No credentials are hardcoded in source code or `compose.yaml`.
- `compose.yaml` reads `DB_NAME`, `DB_USER`, `DB_PASSWORD` from `.env` (via Docker Compose's
  automatic `.env` loading) and passes them into both the `db` and `app` containers.
- `.env.example` is committed with placeholder values only; `.env` is gitignored.
