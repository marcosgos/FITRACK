-- =====================================================================
--  FITRACK · SEED DATA  (development / demo)
-- ---------------------------------------------------------------------
--  Run AFTER schema.sql. Passwords are stored as werkzeug scrypt hashes.
--  Test credentials (email / plain password) for logging in:
--    laura.gomez@example.com    / laura2024
--    carlos.ruiz@example.com    / carlos123
--    marta.sanchez@example.com  / martita22
--    david.torres@example.com   / davidpass
--    elena.vidal@example.com    / elena2000
-- =====================================================================
USE fitrack;

-- ---------------------------------------------------------------------
--  Workout types  (code = stable key the app sends; name = label)
-- ---------------------------------------------------------------------
INSERT INTO workout_types (code, name) VALUES
    ('running',  'Running'),
    ('cycling',  'Cycling'),
    ('swimming', 'Swimming'),
    ('crossfit', 'CrossFit'),
    ('strength', 'Strength'),
    ('free',     'Free'),
    ('mixed',    'Mixed');

-- ---------------------------------------------------------------------
--  Users  (date_of_birth instead of a static age; hashed passwords)
-- ---------------------------------------------------------------------
INSERT INTO users (name, date_of_birth, sex, height_cm, weight_kg, email, password_hash, daily_step_goal) VALUES
    ('Laura Gomez',   '1998-03-12', 'female', 168, 62.50, 'laura.gomez@example.com',   'scrypt:32768:8:1$lmgL3hwjn6GxiVQU$1f962292d6fa844f76a75fa91f60d15ca94b1fa8d6c92f81a8dffd24d81811a140d47b13985f0cd0091745e8a43a3ff7db6b14ad60f630be4cf48e5dded7a316', 10000),
    ('Carlos Ruiz',   '1992-07-25', 'male',   181, 81.20, 'carlos.ruiz@example.com',   'scrypt:32768:8:1$7TN7ycAheg1zT32K$97d5680d0e5acfbe0c92f54e5f30b6add096424fb84b0612bf7e1e97a0563790ed53d91b1ea269f11324bc962db70bf725a3027b54cf2c825825640c8b40a813', 8000),
    ('Marta Sanchez', '2004-11-03', 'female', 160, 55.00, 'marta.sanchez@example.com', 'scrypt:32768:8:1$d6BDrXrkHDDnyE7i$5f2bc2edcef7323031d07e15f3a0c476d244525764b3e3be8a6ce0c515dd5f6079141f3fc862a8fc3aba0e581039d62cec440f991a84436a81f29a7541ea1fc5', 12000),
    ('David Torres',  '1985-01-30', 'male',   175, 90.00, 'david.torres@example.com',  'scrypt:32768:8:1$yuGmKWoqasWCMWHH$6c7b3cbd5113e3a2d7b921b0649f41dcff5731841e90a8c103a457e829ea3be5c4f757d69c6380ad4957bb08306beaa798ecb91414c4e58221e08e087631bf24', 6000),
    ('Elena Vidal',   '1996-09-18', 'female', 170, 65.80, 'elena.vidal@example.com',   'scrypt:32768:8:1$d6zV56mto2QR5tTg$1cb6b5b0568e500e97b1c68ea4d72d830313471356ebcab14cb0f153d51484d9a5c4be5b927b2a63254317ad1c56379a2314e9cce1af1cffbe303bf881ea859c', 9000);

-- ---------------------------------------------------------------------
--  Weight history
-- ---------------------------------------------------------------------
INSERT INTO weight_history (user_id, weight_kg, recorded_on) VALUES
    (1, 63.80, '2026-06-01'), (1, 63.10, '2026-06-15'), (1, 62.50, '2026-07-01'),
    (2, 83.00, '2026-06-01'), (2, 82.10, '2026-06-15'), (2, 81.20, '2026-07-01'),
    (3, 55.50, '2026-06-15'), (3, 55.00, '2026-07-01'),
    (4, 91.50, '2026-06-01'), (4, 90.00, '2026-07-01'),
    (5, 66.50, '2026-06-15'), (5, 65.80, '2026-07-01');

-- ---------------------------------------------------------------------
--  Daily activity
-- ---------------------------------------------------------------------
INSERT INTO daily_activity (user_id, activity_date, steps, calories_burned, sleep_hours) VALUES
    (1, '2026-07-14', 10500, 2200, 7.50), (1, '2026-07-15', 9800, 2100, 6.80), (1, '2026-07-16', 11200, 2350, 8.00),
    (2, '2026-07-14', 7600, 1950, 6.50),  (2, '2026-07-15', 8200, 2000, 7.00), (2, '2026-07-16', 8050, 1980, 6.90),
    (3, '2026-07-14', 13100, 2500, 8.20), (3, '2026-07-15', 12400, 2450, 7.90), (3, '2026-07-16', 12800, 2480, 8.10),
    (4, '2026-07-14', 5200, 1700, 5.50),  (4, '2026-07-15', 6100, 1800, 6.00), (4, '2026-07-16', 5900, 1780, 5.80),
    (5, '2026-07-14', 9300, 2050, 7.20),  (5, '2026-07-15', 9500, 2080, 7.40), (5, '2026-07-16', 9100, 2020, 7.10);

-- ---------------------------------------------------------------------
--  Workouts  (type ids: 1 running, 2 cycling, 3 swimming, 4 crossfit,
--             5 strength, 6 free, 7 mixed)
-- ---------------------------------------------------------------------
INSERT INTO workouts (user_id, workout_type_id, started_at, duration_seconds, avg_heart_rate, steps, calories_burned, notes, is_personal_record, pr_exercise, pr_result) VALUES
    (1, 1, '2026-07-14 07:30:00', 1800, 152, 4200, 320, NULL, FALSE, NULL, NULL),                       -- 1 running
    (1, 3, '2026-07-15 18:00:00', 2400, 138, 0,    280, NULL, FALSE, NULL, NULL),                       -- 2 swimming
    (1, 5, '2026-07-16 07:15:00', 2100, NULL, 0,   350, 'Push day', FALSE, NULL, NULL),                 -- 3 strength
    (2, 2, '2026-07-14 19:00:00', 3600, 130, 0,    410, NULL, FALSE, NULL, NULL),                       -- 4 cycling
    (2, 4, '2026-07-16 19:30:00', 1200, 165, 0,    260, 'AMRAP 20: burpees, KB swings, air squats', TRUE, 'Clean & Jerk', '90 kg / 1 rep'), -- 5 crossfit + PR
    (3, 1, '2026-07-14 06:45:00', 1500, 160, 3600, 290, NULL, FALSE, NULL, NULL);                       -- 6 running (with segments)

-- Strength detail for workout 3
INSERT INTO workout_exercises (workout_id, position, name, sets, reps, weight_kg) VALUES
    (3, 1, 'Bench Press', 4, 8, 60.00),
    (3, 2, 'Overhead Press', 3, 10, 35.00),
    (3, 3, 'Incline Dumbbell Press', 3, 12, 22.00);

-- Running detail (series) for workout 6
INSERT INTO workout_segments (workout_id, position, duration_seconds, distance_m, note) VALUES
    (6, 1, 270, 1000, 'Track intervals'),
    (6, 2, 265, 1000, NULL),
    (6, 3, 280, 1000, 'Last one hard');
