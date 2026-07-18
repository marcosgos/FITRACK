CREATE DATABASE IF NOT EXISTS fitrack;
USE fitrack;

CREATE TABLE usuarios (
    id_usuario      INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    edad            TINYINT UNSIGNED NOT NULL,
    peso            DECIMAL(5,2) NOT NULL COMMENT 'Peso actual en kg',
    correo          VARCHAR(150) NOT NULL UNIQUE,
    contrasena      VARCHAR(255) NOT NULL COMMENT 'Guardar siempre el hash, nunca texto plano',
    objetivo_pasos  INT UNSIGNED NOT NULL DEFAULT 10000 COMMENT 'Meta diaria de pasos',
    fecha_registro  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE historial_peso (
    id_registro     INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario      INT NOT NULL,
    peso            DECIMAL(5,2) NOT NULL,
    fecha           DATE NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    UNIQUE KEY uq_usuario_fecha_peso (id_usuario, fecha)
);

CREATE TABLE actividad_diaria (
    id_actividad        INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario          INT NOT NULL,
    fecha               DATE NOT NULL,
    pasos_diarios       INT UNSIGNED NOT NULL DEFAULT 0,
    calorias_quemadas   INT UNSIGNED NOT NULL DEFAULT 0,
    horas_sueno         DECIMAL(4,2) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Horas dormidas, ej: 7.50',
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    UNIQUE KEY uq_usuario_fecha_actividad (id_usuario, fecha)
);

CREATE TABLE tipos_entrenamiento (
    id_tipo         INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO tipos_entrenamiento (nombre) VALUES
    ('Ciclismo'),
    ('Correr'),
    ('Natacion');

CREATE TABLE entrenamientos (
    id_entrenamiento    INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario          INT NOT NULL,
    id_tipo             INT NOT NULL,
    fecha_inicio        DATETIME NOT NULL,
    duracion_segundos   INT UNSIGNED NOT NULL DEFAULT 0,
    frecuencia_cardiaca INT UNSIGNED NOT NULL COMMENT 'Frecuencia cardiaca media (ppm)',
    pasos               INT UNSIGNED NOT NULL DEFAULT 0,
    calorias_quemadas   INT UNSIGNED NOT NULL DEFAULT 0,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_tipo) REFERENCES tipos_entrenamiento(id_tipo)
);