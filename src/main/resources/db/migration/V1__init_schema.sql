-- =============================================================
-- V1__init_schema.sql
-- PayMate initial database schema
-- Sprint 1: Foundation only — real tables added from Sprint 2
-- =============================================================

-- Enable UUID generation support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Schema version tracking table
-- Stores human-readable version info alongside Flyway's own tracking
CREATE TABLE IF NOT EXISTS schema_info (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version    VARCHAR(50)  NOT NULL,
    applied_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes      TEXT
);

-- Record this migration
INSERT INTO schema_info (version, notes)
VALUES ('1.0.0', 'Initial schema — Sprint 1 foundation');