<div align="center">

# 🏋️ FitAI — AI-Powered Fitness Tracker

**A production-grade microservices fitness application that tracks your workouts and delivers personalized AI coaching via Google Gemini.**

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react)](https://react.dev/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)](https://docs.docker.com/compose/)
[![AWS](https://img.shields.io/badge/AWS-EC2-FF9900?style=for-the-badge&logo=amazonaws)](https://aws.amazon.com/ec2/)
[![Keycloak](https://img.shields.io/badge/Auth-Keycloak-4D4D4D?style=for-the-badge&logo=keycloak)](https://www.keycloak.org/)

**Live Demo → [http://13.223.87.238](http://13.223.87.238)**

</div>

---

## ✨ Features

- 🔐 **Secure Authentication** — OAuth2 / OIDC via Keycloak with JWT token validation across all services
- 🏃 **Activity Tracking** — Log runs, cycling, yoga, swimming, weightlifting and more
- 🔥 **Auto Calorie Calculation** — MET-based formula applied server-side (no manual input required)
- 🤖 **AI Recommendations** — Google Gemini 1.5 Flash analyzes each workout and returns personalized coaching tips
- ⚡ **Event-Driven Architecture** — RabbitMQ decouples activity logging from AI generation (async, non-blocking)
- 📊 **Live Dashboard** — View all activities, stats, and AI recommendations in real-time
- 🐳 **Fully Containerized** — Every service runs in Docker, orchestrated with Docker Compose
- ☁️ **AWS Deployed** — Hosted on EC2 with Nginx serving the React frontend

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      React Frontend (Vite)                       │
│              Login · Register · Dashboard · AI Feed             │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTP (port 80 via Nginx)
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│              Spring Cloud Gateway  :9090                         │
│         JWT validation · Route forwarding · CORS                │
└──────────┬──────────────────┬──────────────────┬───────────────┘
           │                  │                  │
           ▼                  ▼                  ▼
┌──────────────────┐ ┌────────────────┐ ┌────────────────────────┐
│  User Service    │ │Activity Service│ │     AI Service          │
│  :8081           │ │  :8082         │ │     :8083               │
│  Spring Boot     │ │  Spring Boot   │ │     Spring Boot         │
│  PostgreSQL      │ │  MongoDB       │ │     MongoDB             │
│  (Neon)          │ │  (Atlas)       │ │  + Google Gemini API    │
└──────────────────┘ └───────┬────────┘ └──────────┬─────────────┘
                             │                     ▲
                             │   RabbitMQ          │
                             └──── (async) ────────┘
                                  (CloudAMQP)

        All services register with Netflix Eureka  :8761
        All services validate JWT via Keycloak     :8080
```

### Event Flow (Activity → AI Recommendation)

```
User logs activity
      │
      ▼
Activity Service saves to MongoDB
      │
      ▼
Publishes event to RabbitMQ (fitness.exchange → activity.queue)
      │
      ▼
AI Service consumes the message
      │
      ▼
Calls Google Gemini 1.5 Flash with workout context
      │
      ▼
Stores recommendation in MongoDB
      │
      ▼
Frontend polls and displays the recommendation ✅
```

---

## 🧰 Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | React 19, Vite, keycloak-js | SPA, auth flow, dashboard |
| **API Gateway** | Spring Cloud Gateway | Single entry point, JWT validation, routing |
| **Service Discovery** | Netflix Eureka | Dynamic service registration & discovery |
| **Auth** | Keycloak 25 (OAuth2 / OIDC) | Identity provider, JWT issuance |
| **User Service** | Spring Boot 3.3.5, Java 21 | User registration, profile management |
| **Activity Service** | Spring Boot 3.3.5, Java 21 | Activity CRUD, calorie calculation |
| **AI Service** | Spring Boot 3.3.5, Java 21 | Gemini integration, recommendation storage |
| **Message Broker** | RabbitMQ (CloudAMQP) | Async decoupling of activity → AI pipeline |
| **User DB** | PostgreSQL on Neon (cloud) | Persistent user profiles |
| **Activity/AI DB** | MongoDB Atlas (cloud) | Flexible document storage for activities & AI |
| **AI Model** | Google Gemini 1.5 Flash | Personalized fitness recommendations |
| **Containerization** | Docker, Docker Compose | Dev & production orchestration |
| **Web Server** | Nginx (inside Docker) | Serves React build, proxies /api → gateway |
| **Cloud** | AWS EC2 (t3.medium) | Production hosting |

---

## 📁 Project Structure

```
fitAI/
├── 📂 eureka/eureka/              # Service registry (port 8761)
│   ├── Dockerfile
│   └── src/
├── 📂 gateway/gateway/            # API Gateway + JWT security (port 9090)
│   ├── Dockerfile
│   └── src/
├── 📂 userservice/userservice/    # User management + Neon PostgreSQL (port 8081)
│   ├── Dockerfile
│   └── src/
├── 📂 activityservice/            # Activity tracking + MongoDB (port 8082)
│   ├── Dockerfile
│   └── src/
├── 📂 aiservice/aiservice/        # Gemini AI + MongoDB (port 8083)
│   ├── Dockerfile
│   └── src/
├── 📂 frontend/fitai-react/       # React 19 frontend (port 80 in prod)
│   ├── Dockerfile                 # Multi-stage: Vite build → Nginx
│   ├── nginx.conf                 # SPA routing + /api proxy
│   └── src/
│       ├── pages/                 # LoginPage, RegisterPage, DashboardPage
│       ├── components/            # ActivityFeed, AIRecommendations, LogActivityForm, StatsPills
│       ├── api.js                 # All fetch calls to the gateway
│       └── config.js              # VITE_* env var bindings
├── docker-compose.yml             # Local dev (infra only: Keycloak, RabbitMQ, MongoDB, Postgres)
├── docker-compose.prod.yml        # Production (all 7 services + frontend)
├── Dockerfile.keycloak            # Custom Keycloak image
├── setup-keycloak.sh              # One-shot realm + client setup script
└── .env.example                   # Template for environment variables
```

---

## 🚀 Getting Started — Local Development

### Prerequisites

- Java 21
- Docker Desktop
- Node.js 18+
- Maven (or use the `./mvnw` wrapper)

### 1. Clone the Repository

```bash
git clone https://github.com/01anirudh/FitAi.git
cd FitAi
```

### 2. Configure Environment Variables

```bash
cp .env.example .env
# Edit .env with your credentials (see Environment Variables section below)
```

### 3. Start Infrastructure (Keycloak, RabbitMQ, MongoDB, PostgreSQL)

```bash
docker compose up -d
```

Wait ~60 seconds for Keycloak to start, then set up the realm:

```bash
bash setup-keycloak.sh localhost
```

### 4. Load Environment Variables (PowerShell)

```powershell
Get-Content .env |
  Where-Object { $_ -notmatch '^#' -and $_ -match '=' } |
  ForEach-Object {
    $kv = $_ -split '=', 2
    [System.Environment]::SetEnvironmentVariable($kv[0].Trim(), $kv[1].Trim())
  }
```

### 5. Start All Microservices

Open 5 terminals:

```bash
# Terminal 1 — Service Registry
cd eureka/eureka && ./mvnw spring-boot:run

# Terminal 2 — User Service
cd userservice/userservice && ./mvnw spring-boot:run

# Terminal 3 — Activity Service
cd activityservice/activityservice && ./mvnw spring-boot:run

# Terminal 4 — AI Service
cd aiservice/aiservice && ./mvnw spring-boot:run

# Terminal 5 — API Gateway
cd gateway/gateway && ./mvnw spring-boot:run
```

### 6. Start the React Frontend

```bash
cd frontend/fitai-react
npm install
npm run dev
```

Open **[http://localhost:5173](http://localhost:5173)** 🎉

---

## ☁️ AWS Deployment (Production)

All production files are included in the repo. The entire stack runs on a single EC2 instance via Docker Compose.

### Prerequisites

- AWS account
- EC2 instance: **Ubuntu 24.04 LTS**, `t3.medium`, 30 GB storage
- Security Group inbound rules: ports `22`, `80`, `8080`, `9090`

### 1. SSH into EC2 and Install Docker

```bash
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker ubuntu && newgrp docker

# Add swap (prevents OOM on t3.medium)
sudo fallocate -l 4G /swapfile && sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 2. Clone and Configure

```bash
git clone https://github.com/01anirudh/FitAi.git && cd FitAi
nano .env   # Fill in all values — replace <EC2_PUBLIC_IP> with your actual IP
```

### 3. Build and Start Everything

```bash
docker compose -f docker-compose.prod.yml up --build -d
```

> First build takes ~10–15 minutes (compiles all 5 Java services + React).

### 4. Setup Keycloak

```bash
bash setup-keycloak.sh <YOUR_EC2_PUBLIC_IP>
```

Then visit `http://<EC2_IP>:8080/admin` → create your first user in the `fitai-db` realm.

### 5. Access the App

| URL | Service |
|-----|---------|
| `http://<EC2_IP>` | React Frontend |
| `http://<EC2_IP>:8080` | Keycloak Admin |
| `http://<EC2_IP>:8761` | Eureka Dashboard |

---

## 🔑 API Reference

All endpoints are accessed through the API Gateway at `:9090`.

### User Service `/api/users`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/users/keycloak-register` | ❌ Public | Register new user in Keycloak |
| `POST` | `/api/users/sync` | ✅ JWT | Sync Keycloak user into local DB (call after login) |
| `GET` | `/api/users/{userId}` | ✅ JWT | Get user profile |
| `GET` | `/api/users/{userId}/validate` | ✅ JWT | Check if user exists |

### Activity Service `/api/activities`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/activities` | ✅ JWT | Log a new activity |
| `GET` | `/api/activities` | ✅ JWT | Get all activities for the authenticated user |

### AI Service `/api/recommendations`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/recommendations/user/{userId}` | ✅ JWT | Get all AI recommendations for a user |
| `GET` | `/api/recommendations/activity/{activityId}` | ✅ JWT | Get AI recommendation for a specific activity |

### Activity Types

```
RUNNING · CYCLING · SWIMMING · YOGA · WEIGHTLIFTING · HIIT · WALKING · OTHER
```

---

## 🌍 Environment Variables

Copy `.env.example` to `.env` and fill in your values:

```env
# PostgreSQL (Neon recommended)
DB_URL=jdbc:postgresql://<host>/<dbname>?sslmode=require
DB_USERNAME=your_username
DB_PASSWORD=your_password

# MongoDB Atlas
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/

# RabbitMQ (CloudAMQP recommended)
RABBITMQ_URI=amqps://user:pass@host/vhost

# Keycloak
KEYCLOAK_ISSUER_URI=http://<host>:8080/realms/fitai-db
KEYCLOAK_ADMIN_PASSWORD=your_admin_password

# Google Gemini AI
GEMINI_API_KEY=your_gemini_api_key

# Frontend (production only)
VITE_GATEWAY_URL=http://<host>:9090
VITE_KEYCLOAK_URL=http://<host>:8080
VITE_KEYCLOAK_REALM=fitai-db
VITE_KEYCLOAK_CLIENT_ID=fitai-frontend
```

> ⚠️ **Never commit `.env` to Git.** It is listed in `.gitignore`.

---

## 🛠️ Useful Commands

```bash
# View logs for any service
docker logs fitai-gateway -f
docker logs fitai-userservice -f
docker logs fitai-keycloak -f

# Restart a single service (without rebuilding)
docker compose -f docker-compose.prod.yml restart aiservice

# Rebuild and redeploy after a code change
git pull
docker compose -f docker-compose.prod.yml up --build -d

# Stop all services
docker compose -f docker-compose.prod.yml down

# Check service registration in Eureka
curl http://localhost:8761/eureka/apps
```

---

## 🤝 Contributing

Pull requests are welcome! Please open an issue first to discuss what you'd like to change.

---

## 📄 License

MIT
