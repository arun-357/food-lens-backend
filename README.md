# Food Lens Backend

A Spring Boot REST API that powers the Food Lens application. It authenticates users, analyzes food names via Google Gemini, fetches food images from Pixabay, caches results, and enforces daily usage limits.

- Frontend repository: https://github.com/arun-357/food-lens-frontend
- Example frontend URL: https://food-lens-frontend.onrender.com

## Live Demo
- Frontend (Live): https://food-lens-frontend.onrender.com/

## Full-Stack Overview
This project showcases full‑stack application development:
- Frontend: Food Lens Frontend (see repo above) as the SPA client.
- Backend: This Spring Boot service exposing REST APIs for auth and food search.
- Communication: JSON over HTTPS using endpoints under `/auth/*` and `/food/*`.
- Auth: JWT-based; frontend sends `Authorization: Bearer <token>`.
- Data: PostgreSQL via Spring Data JPA.
- Integrations: Google Gemini for content, Pixabay for images.
- Cross-cutting: CORS, caching, rate limiting, and Docker support.

## Features
- JWT-based authentication (register/login)
- Food analysis using Google Gemini (concise description, ingredients, benefits)
- Image search via Pixabay
- Caching: reuses food details; image URL auto-refreshes if expired (24h)
- User history (last 5 searches)
- Daily usage limiter per user
- CORS configurable via environment variables

## Tech Stack
- Java 21, Spring Boot 3 (Web, Security, JPA/Hibernate)
- PostgreSQL
- jjwt 0.11.x (JWT), google-genai (Gemini), Lombok, Maven
- Dockerfile provided

## Getting Started (Local)
### Prerequisites
- Java 21+
- PostgreSQL running locally
- Maven (wrapper included)

### 1) Create database
```bash
createdb -U postgres fooddb
```

### 2) Configure environment
Create a `.env` file under `src/main/resources/` (or export these variables in your shell):
```properties
DB_URL=jdbc:postgresql://localhost:5432/fooddb
DB_USER=postgres
DB_PASS=your_db_password
# Base64-encoded secret (generate one with: openssl rand -base64 64)
JWT_SECRET=base64-secret-here
GEMINI_API_KEY=your_gemini_api_key
PIXABAY_API_KEY=your_pixabay_api_key
CORS_ALLOWED_ORIGINS=https://food-lens-frontend.onrender.com,http://localhost:5173
CORS_ALLOW_CREDENTIALS=true
PORT=8080
```
The application also respects standard Spring variables like `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.

### 3) Run the app
```bash
./mvnw spring-boot:run
```
By default the server runs on `http://localhost:8080`.

## API
Base URL: `http://localhost:8080`

Auth (public):
- POST `/auth/register`
  - Body: `{ "email": "user@example.com", "password": "Password123" }`
  - Response: `{ success, message }`
- POST `/auth/login`
  - Body: `{ "email": "user@example.com", "password": "Password123" }`
  - Response: `{ success, message, accessToken }`

Food (requires Authorization: `Bearer <token>` for search/history recording):
- GET `/food/search?name=<query>`
  - Returns `Food` object. If cached, reuses data. If image URL is missing/expired (>24h), refreshes only the `imageUrl` and updates `imageUrlExpiresAt`.
- GET `/food/history`
  - Returns last 5 searches for the authenticated user.

## Data Model (simplified)
- `User(id, email, password)`
- `Food(id, name, description, ingredients, benefits, imageUrl, imageUrlExpiresAt, isFood)`
- `UserHistory(id, user, food, timestamp)`
- `UserUsage(id, user, date, count)`

## CORS
Configure allowed origins and credentials via:
- `CORS_ALLOWED_ORIGINS` (comma-separated; supports patterns via Spring `allowedOriginPatterns`)
- `CORS_ALLOW_CREDENTIALS` (true/false)

Example for the provided frontend:
```
CORS_ALLOWED_ORIGINS=https://food-lens-frontend.onrender.com,http://localhost:5173
CORS_ALLOW_CREDENTIALS=true
```

## Docker
Build and run with environment variables:
```bash
docker build -t food-lens-backend .

docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/fooddb" \
  -e DB_USER=postgres \
  -e DB_PASS=your_db_password \
  -e JWT_SECRET="base64-secret-here" \
  -e GEMINI_API_KEY=your_gemini_api_key \
  -e PIXABAY_API_KEY=your_pixabay_api_key \
  -e CORS_ALLOWED_ORIGINS="https://food-lens-frontend.onrender.com,http://localhost:5173" \
  -e CORS_ALLOW_CREDENTIALS=true \
  -e PORT=8080 \
  food-lens-backend
```

## Notes & Limits
- Daily usage cap (default 10 searches/user/day).
- Images are fetched from Pixabay; `imageUrl` is valid for 24 hours and tracked via `imageUrlExpiresAt`.
- JWT secret must be Base64-encoded (HS256). Generate one:
```bash
openssl rand -base64 64
```

## Troubleshooting
- `FATAL: database "fooddb" does not exist` → Create the DB: `createdb -U postgres fooddb`.
- Connection/auth errors → Verify `DB_URL`, `DB_USER`, `DB_PASS`, and that PostgreSQL is running.
- CORS errors → Set `CORS_ALLOWED_ORIGINS` to your frontend origin and redeploy/restart.
- Gemini parsing errors → The API response may vary. The backend returns a specific parsing error message.

## License
MIT
