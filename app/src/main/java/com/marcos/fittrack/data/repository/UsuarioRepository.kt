package com.marcos.fittrack.data.repository

import com.marcos.fittrack.data.api.ApiClient
import com.marcos.fittrack.data.model.LoginRequest
import com.marcos.fittrack.data.model.RegistroRequest
import com.marcos.fittrack.data.model.RegistroResponse
import com.marcos.fittrack.data.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.marcos.fittrack.data.model.ActividadDiaria
import com.marcos.fittrack.data.model.EntrenamientoApi
import com.marcos.fittrack.data.model.EntrenamientoRequest
import com.marcos.fittrack.data.model.IdResponse
import com.marcos.fittrack.data.model.TipoEntrenamientoApi

class UsuarioRepository {

    fun login(
        correo: String,
        contrasena: String,
        alExito: (Usuario) -> Unit,
        alError: (String) -> Unit
    ) {
        val llamada = ApiClient.api.login(LoginRequest(correo, contrasena))
        llamada.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    val usuario = response.body()
                    if (usuario != null) alExito(usuario) else alError("Respuesta vacía del servidor")
                } else {
                    alError("Correo o contraseña incorrectos")
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                alError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun registrar(
        datos: RegistroRequest,
        alExito: (Int) -> Unit,
        alError: (String) -> Unit
    ) {
        val llamada = ApiClient.api.registrar(datos)
        llamada.enqueue(object : Callback<RegistroResponse> {
            override fun onResponse(call: Call<RegistroResponse>, response: Response<RegistroResponse>) {
                if (response.isSuccessful) {
                    val cuerpo = response.body()
                    if (cuerpo != null) alExito(cuerpo.id) else alError("Respuesta vacía del servidor")
                } else {
                    alError("No se pudo crear la cuenta. Revisa los datos.")
                }
            }

            override fun onFailure(call: Call<RegistroResponse>, t: Throwable) {
                alError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun obtenerEntrenamientos(
        idUsuario: Int,
        alExito: (List<EntrenamientoApi>) -> Unit,
        alError: (String) -> Unit
    ) {
        ApiClient.api.getEntrenamientos(idUsuario).enqueue(object : Callback<List<EntrenamientoApi>> {
            override fun onResponse(call: Call<List<EntrenamientoApi>>, response: Response<List<EntrenamientoApi>>) {
                if (response.isSuccessful) {
                    alExito(response.body() ?: emptyList())
                } else {
                    alError("No se pudieron cargar los entrenamientos")
                }
            }

            override fun onFailure(call: Call<List<EntrenamientoApi>>, t: Throwable) {
                alError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun obtenerActividad(
        idUsuario: Int,
        alExito: (List<ActividadDiaria>) -> Unit,
        alError: (String) -> Unit
    ) {
        ApiClient.api.getActividad(idUsuario).enqueue(object : Callback<List<ActividadDiaria>> {
            override fun onResponse(call: Call<List<ActividadDiaria>>, response: Response<List<ActividadDiaria>>) {
                if (response.isSuccessful) {
                    alExito(response.body() ?: emptyList())
                } else {
                    alError("No se pudo cargar la actividad")
                }
            }

            override fun onFailure(call: Call<List<ActividadDiaria>>, t: Throwable) {
                alError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun obtenerTiposEntrenamiento(
        alExito: (List<TipoEntrenamientoApi>) -> Unit,
        alError: (String) -> Unit
    ) {
        ApiClient.api.getTiposEntrenamiento().enqueue(object : Callback<List<TipoEntrenamientoApi>> {
            override fun onResponse(call: Call<List<TipoEntrenamientoApi>>, response: Response<List<TipoEntrenamientoApi>>) {
                if (response.isSuccessful) {
                    alExito(response.body() ?: emptyList())
                } else {
                    alError("No se pudieron cargar los tipos de entrenamiento")
                }
            }

            override fun onFailure(call: Call<List<TipoEntrenamientoApi>>, t: Throwable) {
                alError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun guardarEntrenamiento(
        idUsuario: Int,
        datos: EntrenamientoRequest,
        alExito: (Int) -> Unit,
        alError: (String) -> Unit
    ) {
        ApiClient.api.guardarEntrenamiento(idUsuario, datos).enqueue(object : Callback<IdResponse> {
            override fun onResponse(call: Call<IdResponse>, response: Response<IdResponse>) {
                if (response.isSuccessful) {
                    val cuerpo = response.body()
                    if (cuerpo != null) alExito(cuerpo.id) else alError("Respuesta vacía del servidor")
                } else {
                    alError("No se pudo guardar el entrenamiento")
                }
            }

            override fun onFailure(call: Call<IdResponse>, t: Throwable) {
                alError("Error de conexión. Revisa tu internet.")
            }
        })
    }





}