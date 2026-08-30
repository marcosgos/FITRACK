package com.marcos.fittrack.data.api

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
import com.marcos.fittrack.data.model.WorkoutType
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    fun login(@Body request: LoginRequest): Call<User>

    // NUEVO (login con Google): mismo endpoint para login y registro,
    // la API resuelve si la cuenta existe o hay que crearla.
    @POST("auth/google")
    fun loginWithGoogle(@Body request: GoogleAuthRequest): Call<User>

    @POST("users")
    fun register(@Body request: RegisterRequest): Call<IdResponse>

    @PATCH("users/{id}")
    fun updateProfile(@Path("id") userId: Int, @Body request: ProfileUpdateRequest): Call<Map<String, Any>>

    @GET("workout-types")
    fun getWorkoutTypes(): Call<List<WorkoutType>>

    @GET("users/{id}/workouts")
    fun getWorkouts(@Path("id") userId: Int): Call<List<Workout>>

    @GET("users/{id}/workouts/{workoutId}")
    fun getWorkoutDetail(@Path("id") userId: Int, @Path("workoutId") workoutId: Int): Call<Workout>

    @POST("users/{id}/workouts")
    fun saveWorkout(@Path("id") userId: Int, @Body request: WorkoutRequest): Call<IdResponse>

    @GET("users/{id}/activity")
    fun getActivity(@Path("id") userId: Int): Call<List<DailyActivity>>

    // NUEVO: historial de peso (faltaba, existe en la API pero no estaba cableado en la app)
    @GET("users/{id}/weight-history")
    fun getWeightHistory(@Path("id") userId: Int): Call<List<WeightLog>>

    // NUEVO: registrar un nuevo peso
    @POST("users/{id}/weight-history")
    fun addWeightLog(@Path("id") userId: Int, @Body request: WeightLogRequest): Call<IdResponse>
}
