MERGE INTO users (username, password, role)
KEY (username)
VALUES ('admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoO.HH9KpGgIuRrI8bXH/F1C5jY9E8uDZu', 'ROLE_USER');
