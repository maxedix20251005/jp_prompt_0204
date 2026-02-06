# Deployment Instruction

## Environments
- Dev: H2 in-memory database
- Prod: MySQL database

## Prerequisites
- Java 17+
- Maven or Maven Wrapper
- MySQL 8+ for prod

## Build
```
./mvnw.cmd -q -DskipTests package
```

## Run (Dev)
```
./run-dev.ps1
```
- Profile: `dev`
- H2 Console: `/h2-console`

## Run (Prod)
```
./run-prod.ps1
```
- Profile: `prod`
- MySQL connection configured in `application-prod.properties`

## DB Setup (Prod)
```
CREATE DATABASE tododb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Health Check
- App URL: `http://localhost:8080`
- List: `/todos`
- Login: `/login`

## Logs
- Check console output for startup errors.
- Set `logging.level.web=DEBUG` if needed.

## Rollback
- Re-deploy previous artifact.
- Database changes are minimal; schema is managed by JPA `ddl-auto=update`.

## Notes
- In prod, ensure MySQL service is running and credentials are correct.
- For SSL/production, configure HTTPS at reverse proxy or add SSL config.
