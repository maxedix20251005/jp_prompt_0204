# Limitations

- No role-based authorization beyond basic login (all users can access all features).
- No password reset or account management UI.
- No audit logging for changes.
- CSV export always returns all records (no filter-aware export).
- MyBatis + JPA coexist; write operations use JPA, read/paging uses MyBatis.
- H2 dev data is volatile (cleared on restart).
- No multi-tenant support.
- No timezone configuration per user; server time is used.
