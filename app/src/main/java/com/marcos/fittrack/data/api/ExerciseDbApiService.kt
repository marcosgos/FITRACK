package com.marcos.fittrack.data.api

import com.marcos.fittrack.data.model.Ejercicio
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ExerciseDbApiService {
    @GET("hasaneyldrm/exercises-dataset/main/data/exercises.json")
    fun getEjercicios(): Call<List<Ejercicio>>
}

object ExerciseDbApiClient {

    private const val BASE_URL = "https://raw.githubusercontent.com/"

    val api: ExerciseDbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseDbApiService::class.java)
    }
}