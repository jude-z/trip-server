# Trip Server - AI-Powered Trip Planner

A travel planning platform that generates optimized itineraries using machine learning, with group travel coordination, community reviews, and social login.

## Architecture

```
Client (Vercel)
    │
    ├── Spring Boot Server (Main API)
    │       ├── PostgreSQL
    │       ├── Redis
    │       └── AWS S3
    │
    └── FastAPI Server (AI Recommendations)
            └── PostgreSQL
```

- **Spring Boot** — Handles authentication, user management, groups, payments, and all business logic
- **FastAPI** — Runs the AI recommendation engine (K-means clustering + route optimization)

## Tech Stack

### Spring Boot Server

| Category | Technology |
|----------|-----------|
| Framework | Spring Boot 3.4.3, Java 21 |
| Database | PostgreSQL, Spring Data JPA, QueryDSL |
| Cache | Redis |
| Auth | JWT (HS512) + OAuth2 (Google, Naver, Kakao) |
| Storage | AWS S3 |
| Docs | SpringDoc OpenAPI (Swagger) |
| Real-time | WebSocket, SSE, FCM |
| Payment | Toss Payments |

### FastAPI Server

| Category | Technology |
|----------|-----------|
| Framework | FastAPI 0.115.11, Python 3.10+ |
| Database | PostgreSQL (psycopg2) |
| ML | scikit-learn (K-means), geopy |
| Server | Uvicorn |

## Features

### AI Travel Recommendations
- K-means clustering groups destinations by geographic proximity per travel day
- Nearest-neighbor TSP approximation optimizes visit order within each day
- Automatically balances tourist spots (3-5/day) and restaurants (max 2/day)
- Selects nearest accommodation to each day's activity center
- Supports 1-7 day trip generation

### User & Authentication
- Email sign-up with verification
- OAuth2 social login (Google, Naver, Kakao)
- JWT access token + refresh token (HTTP-only cookie)

### Group Travel
- Create and manage travel groups with participant limits
- Join/leave groups with creator approval workflow
- Group likes and comments

### Travel Courses
- Browse pre-designed curated travel courses
- Course reviews and ratings
- Like/bookmark courses

### Community
- Post-trip receipt reviews with photo uploads (S3)
- Nested comment system with depth tracking
- Like/unlike comments

### Payments
- Toss payment gateway integration
- Idempotency key support
- Payment confirmation, cancellation, and history

### Bookmarks & Destinations
- Save/bookmark destinations
- Browse by category hierarchy
- Festival tracking and likes

## Project Structure

```
trip-server/
├── spring-server/
│   └── src/main/java/com/tripplannerai/
│       ├── controller/      # REST endpoints
│       ├── service/         # Business logic
│       ├── entity/          # JPA entities
│       ├── repository/      # Data access
│       ├── dto/             # Request/Response DTOs
│       ├── config/          # Spring configuration
│       ├── common/          # JWT, security, S3, exceptions
│       ├── advice/          # Global exception handlers
│       └── mapper/          # Entity-DTO mappers
│
└── fastapi-server/app/
    ├── main.py              # App entry point
    ├── core/                # Config, DB connection
    ├── api/v1/              # Endpoints & schemas
    ├── services/            # Recommendation engine
    ├── db/                  # SQL queries
    └── utils/               # Clustering, distance calc
```

## API Overview

### Spring Boot

```
POST   /auth/login                    # Sign in
POST   /auth/sign-up                  # Register
POST   /auth/refresh                  # Refresh token
POST   /api/recommend                 # Get AI recommendation
POST   /api/recommend/save            # Save as plan
GET    /api/plan                      # Get user plans
GET    /api/destination/**            # Browse destinations
POST   /api/group                     # Create group
GET    /api/group                     # List groups
PUT    /api/group/{id}/participate    # Join group
POST   /api/confirm                   # Confirm payment
GET    /api/course                    # List courses
POST   /api/keep                      # Bookmark destination
POST   /api/receiptReview             # Post review
GET    /health                        # Health check
```

### FastAPI

```
POST   /api/v1/recommendations                        # Generate itinerary
GET    /api/v1/recommendations/categories/{code}       # Category hierarchy
```

## Getting Started

### Prerequisites
- Java 21
- Python 3.10+
- PostgreSQL
- Redis

### Spring Boot Server

```bash
cd spring-server
cp src/main/resources/application.yml.example src/main/resources/application.yml
# Configure database, Redis, S3, JWT, OAuth2 credentials
./gradlew bootRun
```

### FastAPI Server

1. Create a virtual environment
```bash
cd fastapi-server
python -m venv venv
```

2. Activate the virtual environment

Windows:
```cmd
venv\Scripts\activate
```

macOS/Linux:
```bash
source venv/bin/activate
```

3. Install dependencies
```bash
pip install -r requirements.txt
```

4. Create a `.env` file in the `fastapi-server` directory
```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_USER=<username>
DB_PASSWORD=<password>
DB_NAME=<db_name>

# Logging
LOG_LEVEL=INFO
LOG_DIR=logs
LOG_FILE=app.log
```

5. Run the server
```bash
uvicorn app.main:app --reload
```

## Recommendation Algorithm

```
1. Query destinations matching user's selected categories & region
2. Separate into tourist spots vs restaurants
3. K-means clustering (k = number of travel days)
4. For each cluster/day:
   - Select 3-5 tourist spots + up to 2 restaurants
   - Optimize route order via nearest-neighbor TSP
   - Find nearest accommodation to cluster center
5. Return structured day-by-day itinerary
```

## Environment Variables

### Spring Boot (application.yml)
- `spring.datasource.url` — PostgreSQL connection
- `spring.data.redis.host` — Redis host
- `jwt.secretKey` — JWT signing key (Base64, HS512)
- `cloud.aws.credentials.*` — AWS S3 credentials
- `spring.security.oauth2.client.*` — OAuth2 provider configs

### FastAPI (.env)
- `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, `DB_NAME` — PostgreSQL connection
- `LOG_LEVEL`, `LOG_DIR` — Logging configuration
