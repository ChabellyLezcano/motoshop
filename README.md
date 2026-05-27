# MotoShop — Sprint 0

Esqueleto contenerizado de extremo a extremo del TFM "Plataforma de comercio electrónico de motocicletas con generación sintética de actividad y observabilidad integral".

Este sprint **no implementa funcionalidad de negocio**. Su único objetivo es dejar todas las piezas conectadas, levantables con un solo comando, y con la pipeline de CI funcionando. Sobre este andamiaje se construirán los siguientes sprints.

## Qué incluye este Sprint 0

- **Backend Spring Boot 3.3** (Java 21) con un endpoint `/api/health`, conexión a PostgreSQL configurada vía JPA, Actuator habilitado y dependencia de Micrometer Prometheus lista para el Sprint 4.
- **Frontend Angular 18** standalone, con un único componente que consume el endpoint de salud y muestra el resultado.
- **PostgreSQL 16** como base de datos, con volumen persistente.
- **Docker Compose** que orquesta los tres servicios.
- **GitHub Actions** con dos jobs (backend y frontend) que se ejecutan en cada push y pull request.
- **CORS** preconfigurado para permitir que Angular llame al backend en desarrollo.

## Requisitos previos en tu máquina

- Docker y Docker Compose v2.
- (Opcional, solo si quieres ejecutar fuera de Docker) JDK 21, Maven 3.9+, Node.js 20+.

## Arranque rápido (todo en Docker)

Desde la raíz del proyecto:

```bash
docker compose up --build
```

La primera vez tardará varios minutos porque construye las imágenes (Maven descarga dependencias, npm instala paquetes). Cuando termine, abre:

- Frontend: <http://localhost:4200>
- Backend (salud directa): <http://localhost:8080/api/health>
- Backend (Actuator): <http://localhost:8080/actuator/health>

Si todo está bien, el frontend mostrará un recuadro verde con el estado del backend.

Para detenerlo: `Ctrl+C` y luego `docker compose down` (añade `-v` si quieres borrar también el volumen de PostgreSQL).

## Arranque en local sin Docker (desarrollo)

Útil cuando estás iterando en código y no quieres reconstruir imágenes a cada cambio.

**1) Levanta solo PostgreSQL en Docker:**

```bash
docker compose up db
```

**2) Backend:**

```bash
cd backend
mvn spring-boot:run
```

**3) Frontend (en otra terminal):**

```bash
cd frontend
npm install        # solo la primera vez
npm start
```

## Estructura del repositorio

```
motoshop/
├── backend/                 # Spring Boot
│   ├── src/main/java/com/motoshop/api/
│   │   ├── MotoshopApiApplication.java
│   │   └── health/
│   │       ├── HealthController.java
│   │       └── CorsConfig.java
│   ├── src/main/resources/application.yml
│   ├── src/test/java/...    # test trivial para CI
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                # Angular
│   ├── src/
│   │   ├── app/app.component.ts
│   │   ├── environments/environment.ts
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css
│   ├── package.json
│   ├── angular.json
│   ├── tsconfig.json
│   ├── nginx.conf
│   └── Dockerfile
├── .github/workflows/ci.yml # Pipeline de CI
├── docker-compose.yml
├── .gitignore
└── README.md
```

## Variables de entorno relevantes

El backend lee la configuración de BD y CORS de variables de entorno, con valores por defecto pensados para Docker Compose:

| Variable | Por defecto | Descripción |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/motoshop` | URL de la BD. En Compose se sobrescribe a `jdbc:postgresql://db:5432/motoshop`. |
| `SPRING_DATASOURCE_USERNAME` | `motoshop` | Usuario de BD. |
| `SPRING_DATASOURCE_PASSWORD` | `motoshop` | Contraseña de BD. |
| `APP_CORS_ALLOWED_ORIGIN` | `http://localhost:4200` | Origen permitido para CORS. |

## Comprobaciones manuales

Antes de dar el Sprint 0 por cerrado, verifica:

- [ ] `docker compose up --build` arranca los tres servicios sin errores.
- [ ] `curl http://localhost:8080/api/health` devuelve JSON con `status: UP`.
- [ ] El frontend en `http://localhost:4200` muestra el recuadro verde.
- [ ] `docker compose down` detiene todo limpiamente.
- [ ] Al subir un commit, la pipeline de GitHub Actions se ejecuta y sus dos jobs (backend y frontend) terminan en verde.

## Notas sobre decisiones de diseño

**CORS y puertos.** En este sprint el navegador del usuario llama directamente al backend en `localhost:8080` desde el frontend servido en `localhost:4200`. Se gestiona con CORS abierto al puerto 4200. En sprints posteriores se puede introducir un proxy inverso si conviene unificar puertos.

**Endpoint propio vs Actuator.** Se expone tanto `/api/health` propio como `/actuator/health` de Spring. El primero sirve como contrato de la aplicación; el segundo, como punto de comprobación operativa.

**`ddl-auto: update`.** Pensado para desarrollo: Hibernate ajusta el esquema automáticamente. En sprints posteriores conviene migrar a Flyway o Liquibase para versionar el esquema.

**Sin Maven Wrapper.** El `pom.xml` no incluye `mvnw` para mantener el zip ligero; tanto la pipeline de CI como el Dockerfile usan Maven directamente. Si lo prefieres en local, puedes generarlo con `mvn -N io.takari:maven:wrapper`.

## Próximos pasos (Sprint 1)

- Spring Security + JWT con BCrypt y roles `COMPRADOR` / `ADMIN`.
- Registro público (solo crea compradores) y siembra del administrador inicial al arrancar.
- Entidad `Motorcycle` y CRUD básico en el backend.
- Pantallas de login y listado de catálogo en Angular.
