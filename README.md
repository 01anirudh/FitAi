# 🏋️ FitAI — AI-Powered Fitness Tracker

A full-stack microservices fitness application that tracks your workouts and generates personalized AI recommendations using Google Gemini.

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green) ![React](https://img.shields.io/badge/React-18-blue) ![Docker](https://img.shields.io/badge/Docker-enabled-blue) ![Keycloak](https://img.shields.io/badge/Auth-Keycloak-red)

---

## ✨ Features

- 🔐 **Secure Authentication** via Keycloak (OAuth2 / JWT)
- 🏃 **Activity Tracking** — log runs, cycling, yoga, swimming & more
- 🔥 **Auto Calorie Calculation** — MET-based formula (no manual input)
- 🤖 **AI Recommendations** — Google Gemini analyzes your workout and gives personalized tips
- 📊 **Dashboard** — view all your activities and stats in real-time
- ⚡ **Event-Driven Architecture** — RabbitMQ decouples activity logging from AI generation

---

## 🏗️ Architecture

```
React Frontend (Vite)
        │
        ▼
  API Gateway (Spring Cloud Gateway) :9090
        │
        ├──▶ User Service    (Postgres) :8081
        ├──▶ Activity Service (MongoDB) :8082
        └──▶ AI Service      (MongoDB) :8083
                │
                ├──▶ RabbitMQ (async message queue)
                └──▶ Google Gemini API
```

All services register with **Eureka** for service discovery and are secured with **Keycloak JWT tokens**.

---

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18, Vite |
| Gateway | Spring Cloud Gateway |
| Backend | Spring Boot 3.3.5, Java 21 |
| Auth | Keycloak (OAuth2 / OIDC) |
| Service Discovery | Netflix Eureka |
| Message Broker | RabbitMQ |
| AI | Google Gemini 1.5 Flash |
| Databases | PostgreSQL (users), MongoDB (activities & AI) |
| Containerization | Docker, Docker Compose |

---

## 🚀 Getting Started

### Prerequisites

- Java 21
- Docker Desktop
- Node.js 18+

### 1. Clone the repo

```bash
git clone https://github.com/YOUR_USERNAME/fitAI.git
cd fitAI
```

### 2. Set up environment variables

```bash
cp .env.example .env
# Edit .env with your real credentials
```

### 3. Start infrastructure (Keycloak, RabbitMQ, MongoDB, PostgreSQL)

```bash
docker-compose up -d
```

Wait ~30 seconds for Keycloak to start, then set up the realm:

```bash
# Create realm, client and test user in Keycloak
docker exec fitai-keycloak /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 --realm master \
  --user admin --password admin

docker exec fitai-keycloak /opt/keycloak/bin/kcadm.sh create realms \
  -s realm=fitness -s enabled=true

docker exec fitai-keycloak /opt/keycloak/bin/kcadm.sh create clients \
  -r fitness -s clientId=fitai-frontend \
  -s publicClient=true -s directAccessGrantsEnabled=true \
  -s "webOrigins=[\"*\"]" -s "redirectUris=[\"*\"]"
```

### 4. Load environment variables

```powershell
# PowerShell — run once per session
Get-Content .env |
  Where-Object { $_ -notmatch '^#' -and $_ -match '=' } |
  ForEach-Object {
    $kv = $_ -split '=', 2
    [System.Environment]::SetEnvironmentVariable($kv[0].Trim(), $kv[1].Trim())
  }
```

### 5. Start all microservices (in order)

Open 5 terminals, one per service:

```bash
# Terminal 1
cd eureka/eureka && ./mvnw spring-boot:run

# Terminal 2
cd userservice/userservice && ./mvnw spring-boot:run

# Terminal 3
cd acitvityservice/acitvityservice && ./mvnw spring-boot:run

# Terminal 4
cd aiservice/aiservice && ./mvnw spring-boot:run

# Terminal 5
cd gateway/gateway && ./mvnw spring-boot:run
```

### 6. Start the Frontend

```bash
cd frontend/fitai-react
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173) 🎉

---

## 🔑 API Endpoints

All endpoints go through the Gateway at `http://localhost:9090`.

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/users/register` | ❌ | Register new user |
| `POST` | `/api/users/sync` | ✅ JWT | Sync Keycloak user to DB |
| `GET` | `/api/activities` | ✅ JWT | Get user's activities |
| `POST` | `/api/activities` | ✅ JWT | Log a new activity |

---

## 🌍 Environment Variables

Copy `.env.example` to `.env` and fill in your values:

```env
DB_URL=jdbc:postgresql://localhost:5432/fitAIdb
DB_USERNAME=postgres
DB_PASSWORD=your_password

MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/
RABBITMQ_URI=amqps://user:pass@rabbitmq-host/vhost

KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/fitness
GEMINI_API_KEY=your_google_gemini_api_key
```

> ⚠️ **Never commit `.env` to Git.** It is git-ignored by default.

---

## 📁 Project Structure

```
fitAI/
├── eureka/              # Service registry (port 8761)
├── gateway/             # API Gateway + Security (port 9090)
├── userservice/         # User management + PostgreSQL (port 8081)
├── acitvityservice/     # Activity tracking + MongoDB (port 8082)
├── aiservice/           # Gemini AI recommendations (port 8083)
├── frontend/fitai-react # React frontend (port 5173)
├── docker-compose.yml   # Infrastructure services
├── .env                 # Local secrets (git-ignored)
└── .env.example         # Template for .env
```

---

## 🤝 Contributing

Pull requests are welcome! Please open an issue first to discuss what you'd like to change.

---

## 📄 License

MIT
