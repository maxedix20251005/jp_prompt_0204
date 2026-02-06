# Test Scenarios

## Authentication
- Valid login succeeds.
- Invalid login shows error.
- Logout redirects to login.

## List & Paging
- Default list loads.
- Page size changes list length.
- Sorting works for Title/Due/Priority/Author/Completed.

## CRUD
- Create -> confirm -> complete works.
- Edit updates data.
- Delete removes item.
- Bulk delete removes selected items.

## Overdue & Near Deadline
- Overdue rows highlight red.
- Near-deadline rows highlight yellow.

## CSV Export
- File downloads as `todo_yyyyMMdd.csv`.
- BOM included (Excel readable).
- Columns match spec.
