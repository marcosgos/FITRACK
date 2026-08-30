-- =====================================================================
--  MIGRATION · Add Google Sign-In support
-- ---------------------------------------------------------------------
--  Run this against the EXISTING Aiven database to bring it in line
--  with schema.sql. Do NOT run schema.sql there — it drops and
--  recreates every table, wiping current data.
-- =====================================================================
USE fitrack;

ALTER TABLE users
    MODIFY password_hash VARCHAR(255) NULL COMMENT 'Never store plain text; null for Google-only accounts';

ALTER TABLE users
    ADD COLUMN google_id VARCHAR(255) NULL UNIQUE
        COMMENT 'Google account subject claim, set on first Google sign-in'
        AFTER password_hash;
