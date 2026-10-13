# class-work-web-wk3 — Multi-Container App (HW2)

Spring Boot backend + PostgreSQL + Redis, run together with Docker Compose.

## Services

- **app** — Spring Boot backend (built from the local `Dockerfile`), port `8080`.
- **db** — PostgreSQL 16, port `5432`, data persisted in the named volume `pgdata`, seeded from `db/init.sql` on first start.
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

## Database initialization (seed data)

`db/init.sql` creates the `items` table (if missing) and inserts three seed rows, so the API
returns data right after the first start.

- `compose.yaml` mounts it read-only into the `db` container at
  `/docker-entrypoint-initdb.d/init.sql`. The official Postgres image runs every `.sql` file in
  that folder automatically.
- It runs **only when the data directory is empty**, i.e. the first start with a new `pgdata`
  volume. A normal `docker compose down` / `up` keeps the volume, so the script does not run again
  and no duplicate rows appear.
- To re-run it (reset to seed data only): `docker compose down -v && docker compose up -d`.
- The table definition matches the `Item` entity, so Hibernate (`ddl-auto=update`) finds the table
  already in place.

Check the seed data:

```bash
curl http://localhost:8080/api/items
# -> Seed item 1, Seed item 2, Seed item 3
```

Without Compose, add the same mount to the Postgres container (run from the project root):

```bash
docker run -d --name wk3-postgres --network app-net \
  -e POSTGRES_DB=appdb -e POSTGRES_USER=appuser -e POSTGRES_PASSWORD=<your_password> \
  -v pgdata:/var/lib/postgresql/data \
  -v "$(pwd)/db/init.sql:/docker-entrypoint-initdb.d/init.sql:ro" \
  -p 5432:5432 postgres:16-alpine
```

## Verifying persistence

```bash
docker compose down        # stop and remove containers, volume stays
docker compose up          # start again, no --build needed
curl http://localhost:8080/api/items   # seed items and anything you added are still there
```

Data only disappears if you explicitly run `docker compose down -v`.

## Notes on secrets

- No credentials are hardcoded in source code or `compose.yaml`.
- `compose.yaml` reads `DB_NAME`, `DB_USER`, `DB_PASSWORD` from `.env` (via Docker Compose's
  automatic `.env` loading) and passes them into both the `db` and `app` containers.
- `.env.example` is committed with placeholder values only; `.env` is gitignored.
