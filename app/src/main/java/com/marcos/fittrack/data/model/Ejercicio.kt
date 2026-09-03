package com.marcos.fittrack.data.model

import java.io.Serializable

data class Ejercicio(
    val id: String,
    val name: String,
    val category: String?,
    val body_part: String?,
    val equipment: String?,
    val instructions: Map<String, String>? = null,
    val instruction_steps: Map<String, List<String>>? = null,
    val muscle_group: String?,
    val secondary_muscles: List<String> = emptyList(),
    val target: String?,
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

    /** Pasos en español si existen; si no, cae al inglés como respaldo. */
    fun pasos(): List<String> {
        return instruction_steps?.get("es")?.takeIf { it.isNotEmpty() }
            ?: instruction_steps?.get("en")
            ?: emptyList()
    }
}