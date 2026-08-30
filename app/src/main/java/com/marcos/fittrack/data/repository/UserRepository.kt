package com.marcos.fittrack.data.repository

import com.marcos.fittrack.data.api.ApiClient
import com.marcos.fittrack.data.model.DailyActivity
import com.marcos.fittrack.data.model.GoogleAuthRequest
import com.marcos.fittrack.data.model.IdResponse
import com.marcos.fittrack.data.model.LoginRequest
import com.marcos.fittrack.data.model.ProfileUpdateRequest
import com.marcos.fittrack.data.model.RegisterRequest
import com.marcos.fittrack.data.model.User
import com.marcos.fittrack.data.model.WeightLog
import com.marcos.fittrack.data.model.WeightLogRequest
import com.marcos.fittrack.data.model.Workout
import com.marcos.fittrack.data.model.WorkoutRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    fun login(
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.login(LoginRequest(email, password)).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) onSuccess(user) else onError("Respuesta vacía del servidor")
                } else {
                    onError("Correo o contraseña incorrectos")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    // NUEVO (login con Google): usado tanto desde LoginActivity como desde
    // RegisterActivity; el backend decide si es alta nueva o login.
    fun loginWithGoogle(
        idToken: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.loginWithGoogle(GoogleAuthRequest(idToken)).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) onSuccess(user) else onError("Respuesta vacía del servidor")
                } else {
                    onError("No se pudo iniciar sesión con Google")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun register(
        data: RegisterRequest,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.register(data).enqueue(object : Callback<IdResponse> {
            override fun onResponse(call: Call<IdResponse>, response: Response<IdResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) onSuccess(body.id) else onError("Respuesta vacía del servidor")
                } else {
                    onError("No se pudo crear la cuenta. Revisa los datos.")
                }
            }

            override fun onFailure(call: Call<IdResponse>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun updateProfile(
        userId: Int,
        data: ProfileUpdateRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.updateProfile(userId, data).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) onSuccess() else onError("No se pudo actualizar el perfil")
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun getWorkouts(
        userId: Int,
        onSuccess: (List<Workout>) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.getWorkouts(userId).enqueue(object : Callback<List<Workout>> {
            override fun onResponse(call: Call<List<Workout>>, response: Response<List<Workout>>) {
                if (response.isSuccessful) onSuccess(response.body() ?: emptyList())
                else onError("No se pudieron cargar los entrenamientos")
            }

            override fun onFailure(call: Call<List<Workout>>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun getActivity(
        userId: Int,
        onSuccess: (List<DailyActivity>) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.getActivity(userId).enqueue(object : Callback<List<DailyActivity>> {
            override fun onResponse(call: Call<List<DailyActivity>>, response: Response<List<DailyActivity>>) {
                if (response.isSuccessful) onSuccess(response.body() ?: emptyList())
                else onError("No se pudo cargar la actividad")
            }

            override fun onFailure(call: Call<List<DailyActivity>>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    fun saveWorkout(
        userId: Int,
        data: WorkoutRequest,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.saveWorkout(userId, data).enqueue(object : Callback<IdResponse> {
            override fun onResponse(call: Call<IdResponse>, response: Response<IdResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) onSuccess(body.id) else onError("Respuesta vacía del servidor")
                } else {
                    onError("No se pudo guardar el entrenamiento")
                }
            }

            override fun onFailure(call: Call<IdResponse>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    // NUEVO: historial de peso (faltaba el cableado, la API ya lo soportaba)
    fun getWeightHistory(
        userId: Int,
        onSuccess: (List<WeightLog>) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.getWeightHistory(userId).enqueue(object : Callback<List<WeightLog>> {
            override fun onResponse(call: Call<List<WeightLog>>, response: Response<List<WeightLog>>) {
                if (response.isSuccessful) onSuccess(response.body() ?: emptyList())
                else onError("No se pudo cargar el historial de peso")
            }

            override fun onFailure(call: Call<List<WeightLog>>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }

    // NUEVO: registrar un nuevo peso
    fun addWeightLog(
        userId: Int,
        data: WeightLogRequest,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.api.addWeightLog(userId, data).enqueue(object : Callback<IdResponse> {
            override fun onResponse(call: Call<IdResponse>, response: Response<IdResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) onSuccess(body.id) else onError("Respuesta vacía del servidor")
                } else {
                    onError("No se pudo registrar el peso")
                }
            }

            override fun onFailure(call: Call<IdResponse>, t: Throwable) {
                onError("Error de conexión. Revisa tu internet.")
            }
        })
    }
}
