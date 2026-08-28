package com.marcos.fittrack.data.model

data class Usuario(
    val id_usuario: Int,
    val nombre: String,
    val edad: Int,
    val peso: String,
    val correo: String,
    val objetivo_pasos: Int,
    val fecha_registro: String
)

data class LoginRequest(
    val correo: String,
    val contrasena: String
)

data class ErrorResponse(
    val error: String
)

data class RegistroRequest(
    val nombre: String,
    val edad: Int,
    val peso: Double,
    val correo: String,
    val contrasena: String,
    val objetivo_pasos: Int = 10000
)

data class RegistroResponse(
    val id: Int
)

data class EntrenamientoApi(
    val id_entrenamiento: Int,
    val fecha_inicio: String,
    val duracion_segundos: Int,
    val frecuencia_cardiaca: Int?,
    val pasos: Int?,
    val calorias_quemadas: Int?,
    val tipo: String
)

data class ActividadDiaria(
    val fecha: String,
    val pasos_diarios: Int,
    val calorias_quemadas: Int,
    val horas_sueno: Double? = null
)

data class TipoEntrenamientoApi(
    val id_tipo: Int,
    val nombre: String
)

data class EntrenamientoRequest(
    val id_tipo: Int,
    val fecha_inicio: String,
    val duracion_segundos: Int,
    val frecuencia_cardiaca: Int = 0,
    val pasos: Int = 0,
    val calorias_quemadas: Int = 0
)

data class IdResponse(
    val id: Int
)