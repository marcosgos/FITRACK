package com.marcos.fittrack.data.api

import com.marcos.fittrack.data.model.ActividadDiaria
import com.marcos.fittrack.data.model.EntrenamientoApi
import com.marcos.fittrack.data.model.LoginRequest
import com.marcos.fittrack.data.model.RegistroRequest
import com.marcos.fittrack.data.model.RegistroResponse
import com.marcos.fittrack.data.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<Usuario>

    @POST("usuarios")
    fun registrar(@Body request: RegistroRequest): Call<RegistroResponse>

    @GET("usuarios/{id}/entrenamientos")
    fun getEntrenamientos(@Path("id") idUsuario: Int): Call<List<EntrenamientoApi>>

    @GET("usuarios/{id}/actividad")
    fun getActividad(@Path("id") idUsuario: Int): Call<List<ActividadDiaria>>

    @GET("tipos_entrenamiento")
    fun getTiposEntrenamiento(): Call<List<TipoEntrenamientoApi>>

    @POST("usuarios/{id}/entrenamientos")
    fun guardarEntrenamiento(@Path("id") idUsuario: Int, @Body request: EntrenamientoRequest): Call<IdResponse>
}