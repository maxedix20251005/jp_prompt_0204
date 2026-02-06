# Security & Privacy

## Authentication
- Spring Security 6.x form login.
- Passwords stored as BCrypt hashes.
- Default dev user: `max / password` (created at startup if missing).

## Authorization
- All authenticated users share the same access (ROLE_USER).

## Data Handling
- No PII beyond username (if used as a person name, treat as user data).
- CSV export exposes all records; restrict access by role if needed.

## Transport Security
- Default is HTTP (local dev).
- For production, terminate TLS at reverse proxy or enable HTTPS in app.

## Session
- Default Spring Security session management.

## Recommendations
- Replace default credentials in production.
- Add account management and password reset if required.
- Add role-based authorization for admin actions (bulk delete, export).
