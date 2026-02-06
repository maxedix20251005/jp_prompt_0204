# Release Notes

## v0.0.1-SNAPSHOT (2026-02-06)
- Core ToDo CRUD with JPA
- MyBatis paging and overdue list
- CSV export with BOM (Excel compatible)
- Bulk delete
- Sorting and pagination
- Overdue and near-deadline highlighting
- Dev/Prod profiles (H2/MySQL)
- Spring Security login (custom login page)

## Known Issues
- `admin` password may differ by environment; use `max / password` in dev.
- CSV export is not filter-aware.

## Migration Notes
- Adding Security introduces `users` table
- MyBatis mapping uses `map-underscore-to-camel-case=true`
