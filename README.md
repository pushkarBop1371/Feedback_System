# Ledger — Feedback / Survey Tool

Individual Assessment Project #17 (Feedback / Survey Tool) — built with **Spring Boot** (backend, H2
in-memory database, JWT auth) and **React** (frontend).

Manages two entities:

- **Survey** (parent) — `title`, `question`, `createdDate`
- **Response** (child) — `respondentName`, `answer`, `submittedDate`, and a reference back to its `Survey`

Edge case handled explicitly: the admin-only stats endpoint returns the response count, and — where answers
are numeric (e.g. a 1–10 rating) — the average, min and max. Answers that aren't numbers (free-text
comments) are counted separately and excluded from the average instead of breaking the calculation.

---

## Access model

There is exactly **one** kind of account: **admin**. There is no self-registration and no "normal user"
login — the public doesn't need an account at all.

| Who                          | Can do                                                                 |
|-------------------------------|-------------------------------------------------------------------------|
| **Anyone** (no login)         | View a survey's title/question, and **submit** a response to it        |
| **Admin** (logged in via JWT) | Create, edit, delete surveys; **view** all responses and stats; edit/delete individual responses |

So visitors can browse open surveys and answer them freely, but they can never see who answered what or
what the aggregate results are — only an admin can. All admin actions are protected server-side with a JWT,
not just hidden in the UI (see the API table below).

The frontend reflects this as two separate experiences:
- **Public site** (default view) — browse surveys, submit a response. No login prompts anywhere.
- **Admin Login** page (separate route, reached via the "Admin login" button in the header) → after signing
  in, an **Admin dashboard** with full survey management and response visibility.

---

## Demo admin account (seeded automatically)

| Username | Password   |
|----------|------------|
| `admin`  | `admin123` |

⚠️ This is a demo credential seeded by `DataSeeder.java` for evaluation convenience. Remove/change it and
replace `app.jwt.secret` in `application.properties` with a real secret (e.g. from an environment variable)
before any real deployment — the one checked in is a dev-only placeholder.

---

## Project structure

```
.
├── backend/     Spring Boot 3 / Java 17 REST API, Spring Security (JWT), H2 in-memory DB
└── frontend/    React 18 (Vite) single-page app - public site + admin dashboard
```

---

## Prerequisites

- **Java 17+** and **Maven 3.8+**
- **Node.js 18+** and **npm**

---

## 1. Run the backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.

- Base path: `http://localhost:8080/api`
- H2 console (optional, for inspecting data): `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:feedbackdb`
  - User: `sa`, Password: *(empty)*

The database is in-memory and reseeds the admin account plus two sample surveys every time the app
restarts (see `DataSeeder.java`) — delete that class if you'd rather start empty.

> **If port 8080 is already in use on your machine**, change `server.port` in
> `backend/src/main/resources/application.properties`, then update the matching `BASE_URL` constant at the
> top of `frontend/src/api/client.js` to the same port — that's the only other place it's hardcoded.

### API overview

| Method | Path                              | Auth required | Description                                   |
|--------|------------------------------------|----------------|------------------------------------------------|
| POST   | `/api/auth/login`                  | No             | Admin login, returns a JWT                     |
| GET    | `/api/surveys`                     | No             | List surveys — paginated, optional `?title=` filter |
| GET    | `/api/surveys/{id}`                | No             | Get one survey (title/question only)           |
| POST   | `/api/surveys`                     | **Admin**       | Create a survey                                |
| PUT    | `/api/surveys/{id}`                | **Admin**       | Update a survey                                |
| DELETE | `/api/surveys/{id}`                | **Admin**       | Delete a survey (cascades to its responses)    |
| GET    | `/api/surveys/{id}/responses`      | **Admin**       | List a survey's responses — paginated          |
| GET    | `/api/surveys/{id}/stats`          | **Admin**       | **Edge case**: aggregate stats for a survey    |
| POST   | `/api/responses`                   | No             | Submit a response (body includes `surveyId`)   |
| GET    | `/api/responses`                   | **Admin**       | List all responses — paginated                 |
| GET    | `/api/responses/{id}`              | **Admin**       | Get one response                               |
| PUT    | `/api/responses/{id}`              | **Admin**       | Update a response                              |
| DELETE | `/api/responses/{id}`              | **Admin**       | Delete a response                              |

Pagination params on list endpoints: `?page=0&size=10` (0-indexed).

For admin-only endpoints, send the JWT from `/api/auth/login` as:
```
Authorization: Bearer <token>
```

### Error handling

- `400 Bad Request` — validation failures (missing/invalid fields), malformed JSON, bad query params
- `401 Unauthorized` — no/invalid/expired JWT on an admin endpoint, or wrong login credentials
- `403 Forbidden` — reserved for future role tiers; currently any valid JWT is an admin, so this mainly
  fires if a token is somehow malformed in a way the filter accepts but the role check rejects
- `404 Not Found` — referencing a survey/response id that doesn't exist
- `500 Internal Server Error` — unexpected server-side failure

All errors return a consistent JSON body:

```json
{
  "timestamp": "2026-08-19T10:15:30",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication is required to access this resource. Log in and include a valid Bearer token.",
  "path": "/api/surveys/1/responses"
}
```

### Quick manual test

```bash
# Anyone can view a survey - no token needed
curl http://localhost:8080/api/surveys

# Anyone can submit a response - no token needed
curl -X POST http://localhost:8080/api/responses \
  -H "Content-Type: application/json" \
  -d '{"respondentName":"Asha","answer":"4","surveyId":1}'

# Viewing responses WITHOUT a token -> expect 401
curl -i http://localhost:8080/api/surveys/1/responses

# Log in as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
# -> { "token": "...", "username": "admin", "role": "ADMIN" }

# Now view responses and stats with the token
curl http://localhost:8080/api/surveys/1/responses \
  -H "Authorization: Bearer <token>"

curl http://localhost:8080/api/surveys/1/stats \
  -H "Authorization: Bearer <token>"

# Create a survey as admin
curl -X POST http://localhost:8080/api/surveys \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"Team Lunch","question":"Rate the food out of 5"}'
```

---

## 2. Run the frontend

In a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

The app starts on **http://localhost:5173** and talks to the backend at `http://localhost:8080/api`
(configured in `src/api/client.js`). Make sure the backend is running first.

Build for production with `npm run build` (outputs to `frontend/dist`).

---

## What the UI covers

**Public site (default, no login anywhere on this path)**
- Browse surveys — paginated, filterable by title
- Click a survey → see its question and a response form → submit → confirmation message
- No stats, no other people's responses, no edit/delete controls are ever shown here

**Admin Login** (separate page, reached via the "Admin login" button in the header)
- Username/password form only — there is no registration link, because there's nothing to register for

**Admin dashboard** (only reachable once logged in)
- Full survey list with create/edit/delete
- Per-survey detail: aggregate stats panel (the edge case) and the full response ledger, with edit/delete
  on individual responses
- "Log out" and "View public site" are always available from the header while in the dashboard

The JWT is stored in the browser and attached automatically to every admin request; it's cleared on logout.

---

## Design notes / assumptions

- `createdDate` and `submittedDate` are stamped server-side on creation (`@PrePersist`), not accepted from
  the client, so they can't be spoofed or left inconsistent.
- Deleting a survey cascades and removes its responses (`orphanRemoval = true`), since a response can't
  meaningfully exist without its parent survey.
- The stats edge case treats any answer that parses as a number (e.g. `"7"`, `"9.5"`) as numeric and
  everything else as free text — this keeps the same `answer` field usable for both rating-style and
  open-ended survey questions without a schema change.
- Filtering is implemented on the surveys list (`?title=`); pagination is implemented on every list
  endpoint (surveys, a survey's responses, and all responses).
- **Auth model**: this is intentionally a single-role system — every account in `app_user` is an admin,
  there's no `role` column to manage, and `AppUserDetailsService` grants `ROLE_ADMIN` to any successfully
  loaded user. `SecurityConfig`'s `hasRole("ADMIN")` checks are still explicit per-route (rather than one
  blanket rule) so the authorization model stays easy to read and to extend if a second role is ever added.
- Passwords are hashed with BCrypt (`spring-security-crypto`'s `BCryptPasswordEncoder`) — plaintext
  passwords are never stored or logged.
- The JWT secret in `application.properties` is a placeholder for local development only. Swap it for a
  real secret (ideally from an environment variable, not committed to source control) before deploying
  anywhere real.
- CORS preflight (`OPTIONS`) requests are explicitly permitted for all paths — necessary because attaching
  the `Authorization` header on admin requests forces the browser to preflight them.

## Stretch goals implemented / not implemented

- ✅ **Authentication on write operations** — implemented with Spring Security + JWT, scoped specifically to
  admin actions (managing surveys, viewing/editing/deleting responses). Submitting a response stays public.
- ❌ **Unit tests for core business logic** — left out to keep the submission focused within the assessment's
  time box. The service layer (`SurveyService`, `ResponseService`, `AuthService`) is already isolated from
  the web layer specifically so unit tests could be added on top without refactoring.
- ❌ **Dockerfile** — not included; the app runs directly via `mvn spring-boot:run` / `npm run dev`.
