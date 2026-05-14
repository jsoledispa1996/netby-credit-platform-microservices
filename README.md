# Netby Credit Platform — Microservices

Mini-ecosistema de evaluación de créditos compuesto por:

- **Frontend** Angular (puerto 4200)
- **Microservicio A** — Orquestador de créditos en Quarkus (puerto 8080)
- **Microservicio B** — Servicio de Riesgos gRPC en Quarkus (puerto 9000)
- **Base de datos** PostgreSQL (puerto 5432)

---

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Docker | 24+ |
| Docker Compose | 2.20+ |
| Git | cualquiera |

Verifica que estén instalados:

```bash
docker --version
docker compose version
```

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/jsoledispa1996/netby-credit-platform-microservices.git
cd netby-credit-platform-microservices
```

### 2. Crear el archivo de variables de entorno

```bash
cp .env.example .env
```

El archivo `.env` ya tiene valores por defecto para desarrollo local. Si deseas cambiar las credenciales de la base de datos, edita el archivo:

```
DB_USER=banco
DB_PASSWORD=bancopass
DB_NAME=creditdb
CORS_ORIGINS=http://localhost:4200
BACKEND_URL=http://credit-orchestrator:8080
NGINX_RESOLVER=127.0.0.11
```

### 3. Levantar todos los servicios

```bash
docker compose up -d --build
```

Docker construirá las imágenes y levantará los 4 contenedores. La primera vez tarda entre 3 y 5 minutos porque descarga dependencias de Maven y npm.

### 4. Verificar que todos los servicios están saludables

```bash
docker compose ps
```

Todos deben aparecer con estado `healthy` o `running`:

```
NAME                        STATUS
netby-postgres              healthy
netby-risk-service          healthy
netby-credit-orchestrator   healthy
netby-credit-evaluation-ui  running
```

### 5. Acceder a la aplicación

| Servicio | URL |
|---|---|
| Frontend | http://localhost:4200 |
| API REST (Orquestador) | http://localhost:8080/v1/credit-evaluations |
| Health check Orquestador | http://localhost:8080/q/health |
| Health check Riesgos | http://localhost:8081/q/health |

---

## Detener los servicios

```bash
docker compose down
```

Para detener **y eliminar los datos de la base de datos**:

```bash
docker compose down -v
```

---

## Reiniciar desde cero (rebuild completo)

```bash
docker compose down -v
docker compose up -d --build
```

---

## Estructura del proyecto

```
netby-credit-platform-microservices/
├── docker-compose.yml
├── .env                          # Variables de entorno (no se sube al repo)
├── .env.example                  # Plantilla de variables
├── netby-bd/
│   └── init.sql                  # Script de inicialización de la BD
├── netby-banking-microservices/
│   ├── netby-credit-orchestrator/ # Microservicio A — Quarkus REST + gRPC client
│   └── netby-risk-service/        # Microservicio B — Quarkus gRPC server
└── netby-credit-evaluation-ui/    # Frontend Angular
```

---

## Arquitectura

```
[Angular UI :4200]
       │  HTTP /api/*
       ▼
[nginx proxy]
       │  HTTP
       ▼
[credit-orchestrator :8080]
       │  gRPC (paralelo)
       ▼
[risk-service :9000]

[credit-orchestrator] ──► [PostgreSQL :5432]
```

- El frontend se comunica con el backend a través del proxy nginx en `/api/` para evitar problemas de CORS.
- El orquestador llama a los dos endpoints del servicio de riesgos **en paralelo** via gRPC (HTTP/2), reduciendo la latencia de ~3.5s a ~2s.

---

## Regla de negocio

Una evaluación se **APRUEBA** si:

```
score > 70
Y
(deudaMensual + cuotaEstimada) < salario * 0.40

donde cuotaEstimada = montoSolicitado / (plazoAnios * 12)
```

En caso contrario se **RECHAZA** con el motivo específico.

---

