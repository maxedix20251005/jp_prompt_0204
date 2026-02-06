# Data Guide

## Overview
This document describes the application data model, seed data, and operational data handling for the ToDo app.

## Data Stores
- Dev: H2 in-memory (ephemeral)
- Prod: MySQL (persistent)

## Core Tables
### todos
- id (PK, BIGINT, auto)
- author (VARCHAR(50), NOT NULL)
- title (VARCHAR(100), NOT NULL)
- description (VARCHAR(500), NULL)
- due_date (DATE, NULL)
- priority (INT, NOT NULL, default 1)
- completed (BOOLEAN, NOT NULL, default false)
- created_at (TIMESTAMP, NOT NULL)
- updated_at (TIMESTAMP, NOT NULL)
- version (BIGINT, optimistic lock)

### users
- id (PK, BIGINT, auto)
- username (VARCHAR(100), UNIQUE, NOT NULL)
- password (VARCHAR(200), BCrypt hash, NOT NULL)
- role (VARCHAR(50), NOT NULL)

## Seed Data
- Dev seeds ToDo data via `data-dev.sql`.
- Dev user seeds via `data-dev-users.sql` (idempotent). 
- Initial demo login: `max / password` (created on startup if missing).

## CSV Export
- Endpoint: `GET /todos/export`
- Columns: ID, タイトル, 登録者, ステータス, 作成日
- Encoding: UTF-8 with BOM (Excel-compatible)

## Data Lifecycle
- Dev data is reset on each restart (H2 in-memory).
- Prod data persists in MySQL.

## Notes
- For MyBatis, `map-underscore-to-camel-case=true` is enabled.
- CSV uses simple escaping: commas, quotes, and newlines are quoted and double-quoted.
