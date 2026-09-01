package com.marcos.fittrack.data.repository

import com.marcos.fittrack.data.api.ExerciseDbApiClient
import com.marcos.fittrack.data.model.Ejercicio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object EjercicioRepository {

    private var cache: List<Ejercicio>? = null

    fun obtenerEjercicios(
        alExito: (List<Ejercicio>) -> Unit,
        alError: (String) -> Unit
    ) {
        val cacheados = cache
        if (cacheados != null) {
            alExito(cacheados)
            return
        }

        ExerciseDbApiClient.api.getEjercicios().enqueue(object : Callback<List<Ejercicio>> {
            override fun onResponse(call: Call<List<Ejercicio>>, response: Response<List<Ejercicio>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    cache = lista
                    alExito(lista)
                } else {
                    alError("No se pudo cargar el catálogo de ejercicios")
                }
            }

            override fun onFailure(call: Call<List<Ejercicio>>, t: Throwable) {
                alError("Error de conexión. Revisa tu internet.")
            }
        })
    }
}