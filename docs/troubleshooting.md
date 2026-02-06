# Troubleshooting

## Login Fails
- Ensure user exists in `users` table.
- Dev default user: `max / password`.

## DB Connection Issues (Prod)
- Verify MySQL is running.
- Check credentials in `application-prod.properties`.

## CSV Download Issues
- Confirm `/todos/export` returns HTTP 200.
- Check browser download settings.

## CSS Not Updating
- Hard refresh (Ctrl+F5).
- CSS file link uses cache-busting query.
