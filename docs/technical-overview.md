# Technical Overview

## Architecture
- Spring Boot 3.x MVC
- JPA (write operations)
- MyBatis (read/paging)
- Thymeleaf views

## Key Components
- `TodoController` handles CRUD, paging, export, bulk delete.
- `TodoService` handles business logic and conversion.
- `TodoMapper` provides SQL for paging and overdue list.
- `SecurityConfig` configures Spring Security.
- `CustomUserDetailsService` loads users from DB.

## Routing
- `/login` login page
- `/todos` list
- `/todos/overdue` overdue list
- `/todos/export` CSV export
- `/todos/bulk-delete` bulk delete

## Data Model
- `Todo` entity with optimistic locking (`version`).
- `UserAccount` entity for authentication.

## Frontend
- Bootstrap 5 + custom CSS (`app.css`).
- Thymeleaf templates in `src/main/resources/templates`.

## Build/Run
- Maven wrapper scripts for dev/prod.
