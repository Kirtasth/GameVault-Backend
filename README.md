# GameVault Backend

Welcome to the backend repository for **GameVault**. This is a Spring Boot application that serves as the core API for the GameVault ecosystem, managing data persistence, object storage, and business logic.

## Architecture & Tech Stack

* **Framework:** Java / Spring Boot
* **Database:** PostgreSQL 16
* **Object Storage:** MinIO (S3-Compatible)
* **Testing:** Testcontainers
* **Containerization:** Docker & Docker Compose
* **CI/CD:** GitHub Actions (via Self-Hosted Runners)

## Prerequisites

Before running the project locally, ensure you have the following installed:

* Java 21
* Docker Desktop or Docker Engine
* Maven

## Environment Variables

To run the application, you will need to configure the following environment variables. If you are using Docker Compose, these are passed down to the container automatically.

### Database (PostgreSQL)
* `POSTGRES_USER`
* `POSTGRES_PASSWORD`
* `POSTGRES_DB`
* `POSTGRES_URL`

### Storage (MinIO)
* `MINIO_ACCESS_KEY`
* `MINIO_SECRET_KEY`
* `MINIO_INTERNAL_URL` (e.g., `http://minio:9000`)
* `MINIO_BUCKET_NAME`

### Testing
* `TESTCONTAINERS_HOST_OVERRIDE` (Set to `host.docker.internal` for Docker-in-Docker CI environments)
* `TESTCONTAINERS_RYUK_DISABLED` (Set to `true` in self-hosted runner environments)

## Running Locally

The easiest way to spin up the entire application along with its dependencies is by using Docker Compose.

1. Create a directory for the project
2. Clone the backend repository using `git clone https://github.com/Kirtasth/GameVault-Backend.git`
3. Clone the frontend repository using `git clone https://github.com/Kirtasth/GameVault-Frontend.git`
4. If it's the first time you're running the project, build the Docker images using:
```bash
docker compose build -t gamevault-backend .
docker compose build -t gamevault-frontend .
```
5. Start the infrastructure using `docker compose --profile status up -d`
6. Access the API at `http://localhost:8080`
7. View the Health Check at `http://localhost:8080/actuator/health`
8. Access the MinIO Console at `http://localhost:9001`
9. Connect to the Database at `localhost:5432`

> **Note:** The `compose.yaml` includes an init container that automatically provisions the MinIO buckets and policies for you.

## Testing

This project uses **Testcontainers** to spin up ephemeral Docker containers for integration testing, ensuring the tests run against real instances of PostgreSQL and MinIO rather than mocks.

To run the test suite, execute `./mvnw test` or `./gradlew test` in your terminal.

> **Note:** If you are running tests inside a self-hosted GitHub Actions runner, ensure the `TESTCONTAINERS_HOST_OVERRIDE` environment variable is mapped to the host gateway so the runner container can route traffic to the dynamic Testcontainer instances.

## CI/CD

Continuous Integration and Deployment are handled via GitHub Actions. We use self-hosted runners (`backend-runner`) deployed via Docker Compose to execute our workflows.

The runner mounts the host's `/var/run/docker.sock`, allowing it to spin up Testcontainers and build images natively.

## Compose
```yaml
name: gamevault

services:
  postgres:
    image: postgres:16.13-alpine3.23
    container_name: postgres
    restart: unless-stopped
    volumes:
      - type: tmpfs
        target: /dev/shm
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    environment:
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_DB: ${POSTGRES_DB}
    healthcheck:
      interval: 30s
      timeout: 5s
      retries: 3
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}"]
    networks:
      - gamevault-net

  minio:
    image: cgr.dev/chainguard/minio:latest
    container_name: minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_ROOT_USER}
      MINIO_ROOT_PASSWORD: ${MINIO_ROOT_PASSWORD}
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"
    restart: unless-stopped
    networks:
      - gamevault-net
    healthcheck:
      test: [ "CMD", "mc", "ready", "local" ]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  minio-init:
    image: cgr.dev/chainguard/minio:latest
    container_name: minio-init
    environment:
      MINIO_ROOT_USER: ${MINIO_ROOT_USER}
      MINIO_ROOT_PASSWORD: ${MINIO_ROOT_PASSWORD}
      MINIO_ACCESS_KEY: ${MINIO_ACCESS_KEY}
      MINIO_SECRET_KEY: ${MINIO_SECRET_KEY}
      MINIO_BUCKET_NAME: ${MINIO_BUCKET_NAME}
    profiles: ["status"]
    depends_on:
      minio:
        condition: service_started
    networks:
      - gamevault-net
    entrypoint: >
      /bin/sh -c "
      echo 'Waiting for MinIO...';
      until /usr/bin/mc alias set myminio http://minio:9000 ${MINIO_ROOT_USER} ${MINIO_ROOT_PASSWORD}; do sleep 2; done;

      echo 'Creating bucket...';
      /usr/bin/mc mb myminio/${MINIO_BUCKET_NAME} --ignore-existing;

      echo 'Setting public read policy for frontend images...';
      /usr/bin/mc anonymous set download myminio/${MINIO_BUCKET_NAME};

      echo 'Creating dedicated backend access keys...';
      /usr/bin/mc admin user add myminio ${MINIO_ACCESS_KEY} ${MINIO_SECRET_KEY};
      /usr/bin/mc admin policy attach myminio readwrite --user ${MINIO_ACCESS_KEY};

      echo 'MinIO setup complete!';
      exit 0;
      "
  backend:
    image: gamevault-backend:latest
    container_name: backend
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      # PostgreSQL connection
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_URL: ${POSTGRES_URL}
      # MinIO connection
      MINIO_ACCESS_KEY: ${MINIO_ACCESS_KEY}
      MINIO_SECRET_KEY: ${MINIO_SECRET_KEY}
      MINIO_INTERNAL_URL: ${MINIO_INTERNAL_URL}
      MINIO_BUCKET_NAME: ${MINIO_BUCKET_NAME}
    volumes:
      - backend_data:/app/jwt-keys
    depends_on:
      postgres:
        condition: service_healthy
      minio:
        condition: service_healthy
    networks:
      - gamevault-net
    healthcheck:
      test: [ "CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1" ]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 60s

  frontend:
    image: gamevault-frontend:latest
    container_name: frontend
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      backend:
        condition: service_healthy
    networks:
      - gamevault-net
    healthcheck:
      test: [ "CMD-SHELL", "curl -f http://localhost/ || exit 1" ]
      interval: 10s
      timeout: 5s
      retries: 3
      start_period: 10s

  ngrok:
    image: ngrok/ngrok:latest
    container_name: ngrok
    restart: unless-stopped
    environment:
      NGROK_AUTHTOKEN: ${NGROK_AUTHTOKEN}
    command: "http frontend:80"
    depends_on:
      - frontend
    ports:
      - "4040:4040"
    networks:
      - gamevault-net

  backend-runner:
    image: myoung34/github-runner:latest
    container_name: backend-runner
    restart: unless-stopped
    environment:
      REPO_URL: https://github.com/Kirtasth/GameVault-Backend
      RUNNER_NAME: backend-runner
      ACCESS_TOKEN: ${RUNNER_PAT_TOKEN}
      LABELS: local,docker
      # Testcontainers connection
      TESTCONTAINERS_HOST_OVERRIDE: "host.docker.internal"
      TESTCONTAINERS_RYUK_DISABLED: "true"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./:/infra
    extra_hosts:
      - "host.docker.internal:host-gateway"
    depends_on:
      backend:
          condition: service_healthy
    networks:
      - gamevault-net

  frontend-runner:
    image: myoung34/github-runner:latest
    container_name: frontend-runner
    restart: unless-stopped
    environment:
      REPO_URL: https://github.com/Kirtasth/GameVault-Frontend
      RUNNER_NAME: frontend-runner
      ACCESS_TOKEN: ${RUNNER_PAT_TOKEN}
      LABELS: local,docker
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./:/infra
    depends_on:
      frontend:
          condition: service_healthy
    networks:
      - gamevault-net

networks:
  gamevault-net:
    driver: bridge

volumes:
  postgres_data:
  minio_data:
  backend_data:
```