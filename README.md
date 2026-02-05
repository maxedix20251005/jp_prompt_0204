# Todo App

Spring Boot ToDo application with H2 (dev) and MySQL (prod) profiles.

## Profiles

### Dev (H2)
- Uses in-memory H2 database
- H2 console enabled at `/h2-console`

Run:
```powershell
cd c:\academia\src\jp_prompt_0204
.\mvnw.cmd spring-boot:run
```

### Prod (MySQL)
- Uses MySQL database
- Settings are in `src/main/resources/application-prod.properties`

Run (recommended):
```powershell
cd c:\academia\src\jp_prompt_0204
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

Alternative:
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod
```

### Switch default profile
The default profile is set in:
`src/main/resources/application.properties`

Current default:
```properties
spring.profiles.active=prod
```

You can override at runtime with:
- Environment variable: `SPRING_PROFILES_ACTIVE=dev`
- CLI option: `--spring.profiles.active=dev`

## MySQL Setup

1. Make sure MySQL is running on `localhost:3306`.
2. Create the database:
```sql
CREATE DATABASE tododb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
3. Confirm user/password match in `application-prod.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=password
```

## Notes
- Dev uses H2 in-memory DB, so data resets on restart.
- Prod uses MySQL for persistence.

## Windows Tips

### Set profile via environment variable (current session)
```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
```

### Set profile persistently (user level)
```powershell
[Environment]::SetEnvironmentVariable("SPRING_PROFILES_ACTIVE", "dev", "User")
```
After setting, reopen the terminal or log out/in.

### H2 Console (dev only)
1. Run with `dev` profile.
2. Open `http://localhost:8080/h2-console`
3. Use JDBC URL: `jdbc:h2:mem:tododb` and user `sa` (no password).
