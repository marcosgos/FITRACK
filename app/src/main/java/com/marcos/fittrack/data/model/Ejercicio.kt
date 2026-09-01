package com.marcos.fittrack.data.model

import java.io.Serializable

data class Ejercicio(
    val id: String,
    val name: String,
    val category: String?,
    val body_part: String?,
    val equipment: String?,
    val muscle_group: String?,
    val secondary_muscles: List<String> = emptyList(),
    val target: String?,
    val instructions_es: String?,
    val instructions_en: String?,
    val image: String?,
    val gif_url: String?,
    val media_id: String?,
    val attribution: String?
) : Serializable {

    companion object {
        private const val BASE_MEDIA_URL = "https://raw.githubusercontent.com/hasaneyldrm/exercises-dataset/main/"
    }

    fun urlImagen(): String? = image?.let { BASE_MEDIA_URL + it }
    fun urlGif(): String? = gif_url?.let { BASE_MEDIA_URL + it }

    /** Instrucciones en español si existen; si no, cae al inglés como respaldo. */
    fun descripcion(): String =
        instructions_es?.takeIf { it.isNotBlank() } ?: instructions_en ?: ""
}