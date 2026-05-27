# MotoShop

Plataforma de comercio electrónico de motocicletas con generación
sintética de actividad y observabilidad integral. TFM del Máster en
Ingeniería Web — UPM.

> **Estado actual: final del Sprint 1.**
> Identidad (JWT, roles BUYER/ADMIN), catálogo navegable con CRUD de
> administración, esquema gobernado por Flyway, Swagger UI integrado,
> tests unitarios e integración con Postgres real.

## Tabla de contenidos

- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Arranque rápido](#arranque-rápido)
- [Variables de entorno](#variables-de-entorno)
- [Documentación de la API (Swagger)](#documentación-de-la-api-swagger)
- [Endpoints principales](#endpoints-principales)
- [Cuenta de administrador](#cuenta-de-administrador)
- [Tests](#tests)
- [Desarrollo local sin Docker](#desarrollo-local-sin-docker)
- [Estructura del repositorio](#estructura-del-repositorio)

## Arquitectura

| Componente   | Tecnología                  | Puerto local |
|--------------|-----------------------------|--------------|
| Frontend     | Angular 18                  | 4200         |
| Backend      | Spring Boot 3.3 (Java 21)   | 8080         |
| Base de datos| PostgreSQL 16               | 5432         |

La autenticación se hace con tokens JWT (HS256). Las contraseñas se
almacenan con BCrypt. El esquema de la base lo gestiona **Flyway**;
Hibernate solo valida que el mapeo cuadra con las tablas reales.

## Requisitos previos

- [Docker](https://www.docker.com/) 24+ y Docker Compose v2.
- Para desarrollo backend nativo: **Java 21** y **Maven 3.9+**.
- Para desarrollo frontend nativo: **Node.js 20+**.

## Arranque rápido

```bash
# 1. Clonar y entrar al repo
git clone <repo-url>
cd motoshop

# 2. (Opcional) Crear un .env personalizado
cp .env.example .env
# Edita .env si quieres cambiar el secreto JWT, la contraseña del admin, etc.

# 3. Levantar todo
docker compose up -d --build

# 4. Esperar a que el backend esté sano (~15s)
docker compose ps

# 5. Comprobar
curl http://localhost:8080/api/health
```

Una vez los tres servicios estén `healthy`:

- **API**: <http://localhost:8080>
- **Swagger UI**: <http://localhost:8080/swagger-ui.html>
- **Tienda (Angular)**: <http://localhost:4200>

Para parar:

```bash
docker compose down            # mantiene los datos
docker compose down -v         # borra también el volumen de Postgres
```

## Variables de entorno

Todas tienen valores por defecto seguros para desarrollo. En producción
hay que sobreescribir, como mínimo, `APP_JWT_SECRET` y
`APP_ADMIN_PASSWORD`.

| Variable                       | Default                          | Descripción                                            |
|--------------------------------|----------------------------------|--------------------------------------------------------|
| `POSTGRES_DB`                  | `motoshop`                       | Nombre de la base de datos                             |
| `POSTGRES_USER`                | `motoshop`                       | Usuario de Postgres                                    |
| `POSTGRES_PASSWORD`            | `motoshop`                       | Contraseña de Postgres                                 |
| `APP_JWT_SECRET`               | `dev-only-secret-change-me-...`  | Clave HMAC del JWT. **Mínimo 32 bytes.**               |
| `APP_JWT_EXPIRATION_MINUTES`   | `60`                             | Duración del token de acceso                           |
| `APP_ADMIN_EMAIL`              | `[email protected]`             | Email del administrador inicial                        |
| `APP_ADMIN_PASSWORD`           | `changeme-admin`                 | Contraseña del administrador inicial                   |
| `APP_CORS_ALLOWED_ORIGINS`     | `http://localhost:4200,...:4300` | Orígenes admitidos, separados por comas                |

Para generar un secreto JWT robusto:

```bash
openssl rand -base64 48
```

## Documentación de la API (Swagger)

La API se documenta automáticamente con OpenAPI 3.

- UI interactiva: <http://localhost:8080/swagger-ui.html>
- Contrato JSON: <http://localhost:8080/v3/api-docs>

Para autenticarte en Swagger UI:

1. Ejecuta `POST /api/auth/login` con `[email protected]` /
   `changeme-admin` y copia el `token` de la respuesta.
2. Pulsa el botón **Authorize** arriba a la derecha.
3. Pega `Bearer <token>` y confirma.
4. A partir de ahí, cualquier endpoint protegido se ejecuta con el
   token automáticamente.

El contrato OpenAPI también puede importarse en Insomnia o Postman
desde la URL `/v3/api-docs`.

## Endpoints principales

### Públicos (sin autenticación)

| Método | Ruta                          | Descripción                                  |
|--------|-------------------------------|----------------------------------------------|
| GET    | `/api/health`                 | Health check                                 |
| POST   | `/api/auth/register`          | Registro (siempre crea rol BUYER)            |
| POST   | `/api/auth/login`             | Login con email/contraseña                   |
| GET    | `/api/motorcycles`            | Listado paginado con filtros opcionales      |
| GET    | `/api/motorcycles/{id}`       | Ficha de una motocicleta                     |

### Autenticados

| Método | Ruta                          | Rol requerido | Descripción                          |
|--------|-------------------------------|---------------|--------------------------------------|
| GET    | `/api/auth/me`                | cualquier rol | Datos del usuario actual             |
| POST   | `/api/motorcycles`            | ADMIN         | Crear una motocicleta                |
| PUT    | `/api/motorcycles/{id}`       | ADMIN         | Actualizar (parcial) una motocicleta |
| DELETE | `/api/motorcycles/{id}`       | ADMIN         | Borrar una motocicleta               |

Filtros disponibles en `GET /api/motorcycles`:
`?q=...&brand=...&category=NAKED&license=A2&minPriceCents=...&maxPriceCents=...&inStock=true&page=0&size=20&sort=priceCents,asc`

## Cuenta de administrador

Al arrancar por primera vez, el backend siembra un único usuario
administrador a partir de `APP_ADMIN_EMAIL` y `APP_ADMIN_PASSWORD`. Si
ese usuario ya existe en la base, **no se modifica**: el seeder es
idempotente, no resetea la contraseña en cada arranque.

El registro público (`POST /api/auth/register`) crea siempre usuarios
con rol BUYER, ignorando cualquier intento del cliente de inyectar
`role`. La promoción a ADMIN llegará en el Sprint 2 mediante un
endpoint protegido del back-office.

## Tests

```bash
cd backend

# Solo tests unitarios (rápidos, ~10-20s, no requieren Docker)
mvn test

# Toda la suite, incluida integración con Postgres real via Testcontainers
mvn verify
```

Los tests unitarios usan el perfil `test` con H2 en memoria. Los de
integración (`*IT.java`) usan el perfil `integration-test` y arrancan
un contenedor Postgres efímero con Testcontainers, ejecutan las
migraciones reales de Flyway y validan el flujo extremo a extremo.

## Desarrollo local sin Docker

Útil cuando se itera sobre el backend o el frontend y se quiere
recargar más rápido.

**Solo Postgres en Docker, backend y frontend nativos:**

```bash
# Postgres aislado
docker compose up -d db

# Backend con perfil de desarrollo
cd backend
mvn spring-boot:run
# Variables de entorno respetadas; los defaults de application.yml apuntan
# a localhost:5432, que es lo que expone el Postgres del compose.

# Frontend (en otra terminal)
cd frontend
npm install
npm start
```

## Estructura del repositorio

```
motoshop/
├── backend/                    # API Spring Boot
│   ├── src/main/java/com/motoshop/api/
│   │   ├── auth/               # Registro, login, /me
│   │   ├── bootstrap/          # Siembra del admin inicial
│   │   ├── catalog/            # Motocicletas (entidad, repo, servicio, DTOs)
│   │   ├── security/           # JWT, filtro, configuración de Spring Security
│   │   ├── user/               # Entidad User y repositorio
│   │   └── web/                # OpenAPI, manejo global de errores
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/       # Migraciones Flyway (V1, V2)
├── frontend/                   # Aplicación Angular
├── .github/workflows/          # Pipelines de GitHub Actions
├── docker-compose.yml
├── .env.example
└── README.md
```