-- =====================================================================
--  MIGRATION · Add workout detail columns (heart rate, distance, speed,
--  elevation, swim metrics)
-- ---------------------------------------------------------------------
--  Run this against the EXISTING Aiven database to bring it in line
--  with schema.sql. Do NOT run schema.sql there — it drops and
--  recreates every table, wiping current data.
-- =====================================================================
USE fitrack;

ALTER TABLE workouts
    ADD COLUMN max_heart_rate SMALLINT UNSIGNED NULL
        COMMENT 'Peak bpm, null if not measured'
        AFTER avg_heart_rate;

ALTER TABLE workouts
    ADD COLUMN distance_m INT UNSIGNED NULL
        COMMENT 'Total distance in metres'
        AFTER calories_burned;

ALTER TABLE workouts
    ADD COLUMN avg_speed_kmh DECIMAL(5,2) NULL
        AFTER distance_m;

ALTER TABLE workouts
    ADD COLUMN max_speed_kmh DECIMAL(5,2) NULL
        AFTER avg_speed_kmh;

ALTER TABLE workouts
    ADD COLUMN elevation_gain_m INT UNSIGNED NULL
        COMMENT 'Positive elevation gain in metres'
        AFTER max_speed_kmh;

ALTER TABLE workouts
    ADD COLUMN swim_style VARCHAR(20) NULL
        COMMENT 'freestyle, backstroke, breaststroke, butterfly'
        AFTER elevation_gain_m;

ALTER TABLE workouts
    ADD COLUMN pool_lengths SMALLINT UNSIGNED NULL
        AFTER swim_style;

ALTER TABLE workouts
    ADD COLUMN pool_length_m DECIMAL(5,2) NULL
        AFTER pool_lengths;

ALTER TABLE workouts
    ADD COLUMN swolf SMALLINT UNSIGNED NULL
        AFTER pool_length_m;
