-- =====================================================================
--  FITRACK · DATABASE SCHEMA
-- ---------------------------------------------------------------------
--  MySQL 8+ / Aiven. English, normalized, and built to grow.
--
--  This script RECREATES the schema from scratch (development reset).
--  If you ever need to keep real production data, write an ALTER-based
--  migration instead of running this file.
--
--  Units convention (kept consistent everywhere):
--    weight   -> kilograms (weight_kg)
--    height   -> centimetres (height_cm)
--    distance -> metres (distance_m)
--    duration -> seconds (duration_seconds)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS fitrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE fitrack;

-- Drop in reverse dependency order so foreign keys don't block the reset.
DROP TABLE IF EXISTS workout_segments;
DROP TABLE IF EXISTS workout_exercises;
DROP TABLE IF EXISTS workouts;
DROP TABLE IF EXISTS workout_types;
DROP TABLE IF EXISTS daily_activity;
DROP TABLE IF EXISTS weight_history;
DROP TABLE IF EXISTS users;


-- ---------------------------------------------------------------------
--  users
--  Stores date_of_birth (not a static age) so age is always derived.
--  Profile fields (sex, height, weight) are nullable: they are filled
--  in later by the personal-data screen, not at sign-up.
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id         INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)     NOT NULL,
    date_of_birth   DATE             NULL,
    sex             ENUM('male','female','other') NULL,
    height_cm       SMALLINT UNSIGNED NULL          COMMENT 'Height in centimetres',
    weight_kg       DECIMAL(5,2)     NULL           COMMENT 'Current weight in kg (history in weight_history)',
    email           VARCHAR(150)     NOT NULL UNIQUE,
    -- NUEVO (login con Google): NULL para cuentas creadas solo con Google,
    -- que no tienen contraseña propia (ver google_id más abajo).
    password_hash   VARCHAR(255)     NULL           COMMENT 'Never store plain text; null for Google-only accounts',
    -- NUEVO: subject (sub) de Google, para encontrar/vincular la cuenta en /auth/google.
    google_id       VARCHAR(255)     NULL UNIQUE    COMMENT 'Google account subject claim, set on first Google sign-in',
    daily_step_goal INT UNSIGNED     NOT NULL DEFAULT 10000,
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);


-- ---------------------------------------------------------------------
--  weight_history
--  One weigh-in per user per day (source of truth for weight over time).
-- ---------------------------------------------------------------------
CREATE TABLE weight_history (
    weight_log_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT          NOT NULL,
    weight_kg       DECIMAL(5,2) NOT NULL,
    recorded_on     DATE         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_weight_date (user_id, recorded_on)
);


-- ---------------------------------------------------------------------
--  daily_activity
--  Passive daily totals (steps / calories / sleep). One row per day.
-- ---------------------------------------------------------------------
CREATE TABLE daily_activity (
    activity_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT          NOT NULL,
    activity_date   DATE         NOT NULL,
    steps           INT UNSIGNED NOT NULL DEFAULT 0,
    calories_burned INT UNSIGNED NOT NULL DEFAULT 0,
    sleep_hours     DECIMAL(4,2) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'e.g. 7.50',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_activity_date (user_id, activity_date)
);


-- ---------------------------------------------------------------------
--  workout_types
--  `code` is the stable machine key the app matches on (never renames).
--  `name` is the human label and can be edited/translated freely.
-- ---------------------------------------------------------------------
CREATE TABLE workout_types (
    workout_type_id INT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(30) NOT NULL UNIQUE COMMENT 'Stable key: running, strength, crossfit...',
    name            VARCHAR(50) NOT NULL UNIQUE COMMENT 'Display label'
);


-- ---------------------------------------------------------------------
--  workouts  (header / summary row for a single session)
--  Detail lives in workout_exercises (strength) and workout_segments
--  (running). notes + PR fields cover crossfit / free / mixed sessions.
-- ---------------------------------------------------------------------
CREATE TABLE workouts (
    workout_id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id            INT      NOT NULL,
    workout_type_id    INT      NOT NULL,
    started_at         DATETIME NOT NULL,
    duration_seconds   INT UNSIGNED NOT NULL DEFAULT 0,
    avg_heart_rate     SMALLINT UNSIGNED NULL COMMENT 'Average bpm, null if not measured',
    max_heart_rate     SMALLINT UNSIGNED NULL COMMENT 'Peak bpm, null if not measured',
    steps              INT UNSIGNED NOT NULL DEFAULT 0,
    calories_burned    INT UNSIGNED NOT NULL DEFAULT 0,
    distance_m         INT UNSIGNED NULL COMMENT 'Total distance in metres',
    avg_speed_kmh      DECIMAL(5,2) NULL,
    max_speed_kmh      DECIMAL(5,2) NULL,
    elevation_gain_m   INT UNSIGNED NULL COMMENT 'Positive elevation gain in metres',
    swim_style         VARCHAR(20) NULL COMMENT 'freestyle, backstroke, breaststroke, butterfly',
    pool_lengths       SMALLINT UNSIGNED NULL,
    pool_length_m      DECIMAL(5,2) NULL,
    swolf              SMALLINT UNSIGNED NULL,
    notes              TEXT NULL COMMENT 'WOD description / free-text notes',
    is_personal_record BOOLEAN NOT NULL DEFAULT FALSE,
    pr_exercise        VARCHAR(100) NULL COMMENT 'PR lift name, e.g. Clean & Jerk',
    pr_result          VARCHAR(100) NULL COMMENT 'PR mark, e.g. 90 kg / 1 rep',
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (workout_type_id) REFERENCES workout_types(workout_type_id),
    INDEX idx_workouts_user_date (user_id, started_at)
);

-- ---------------------------------------------------------------------
--  workout_exercises  (strength detail: one row per exercise)
--  Aggregate sets/reps/weight per exercise, matching the app's UI.
-- ---------------------------------------------------------------------
CREATE TABLE workout_exercises (
    exercise_id INT AUTO_INCREMENT PRIMARY KEY,
    workout_id  INT NOT NULL,
    position    TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Display order within the workout',
    name        VARCHAR(100) NOT NULL,
    sets        SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    reps        SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    weight_kg   DECIMAL(6,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (workout_id) REFERENCES workouts(workout_id) ON DELETE CASCADE
);


-- ---------------------------------------------------------------------
--  workout_segments  (running detail: one row per series / split)
-- ---------------------------------------------------------------------
CREATE TABLE workout_segments (
    segment_id       INT AUTO_INCREMENT PRIMARY KEY,
    workout_id       INT NOT NULL,
    position         TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Series number / order',
    duration_seconds INT UNSIGNED NOT NULL DEFAULT 0,
    distance_m       INT UNSIGNED NULL COMMENT 'Distance in metres',
    note             VARCHAR(255) NULL,
    FOREIGN KEY (workout_id) REFERENCES workouts(workout_id) ON DELETE CASCADE
);
