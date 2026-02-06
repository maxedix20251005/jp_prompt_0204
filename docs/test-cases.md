# Test Cases

## TC-LOGIN-001
- Precondition: user exists (max/password)
- Steps: open /login, enter credentials, submit
- Expected: redirect to /todos, no error

## TC-LOGIN-002
- Steps: wrong password
- Expected: error message displayed

## TC-LIST-001
- Steps: open /todos
- Expected: list renders, pagination visible when needed

## TC-LIST-002
- Steps: change page size
- Expected: list size updates, range text updates

## TC-LIST-003
- Steps: click Title sort twice
- Expected: ascending then descending

## TC-CRUD-001
- Steps: create new item
- Expected: item appears in list

## TC-CRUD-002
- Steps: edit item
- Expected: updated values persist

## TC-CRUD-003
- Steps: delete item
- Expected: item removed

## TC-BULK-001
- Steps: select multiple items, bulk delete
- Expected: all selected removed

## TC-OVERDUE-001
- Steps: open overdue view
- Expected: only overdue items shown

## TC-EXPORT-001
- Steps: click CSV download
- Expected: file name todo_yyyyMMdd.csv

## TC-EXPORT-002
- Steps: open in Excel
- Expected: no mojibake
