package com.marcos.fittrack.data.model

import com.google.gson.annotations.SerializedName

// ===================== Users =====================

data class User(
    @SerializedName("user_id") val userId: Int,
    val name: String,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    val sex: String?,
    @SerializedName("height_cm") val heightCm: Int?,
    @SerializedName("weight_kg") val weightKg: Double?,
    val email: String,
    @SerializedName("daily_step_goal") val dailyStepGoal: Int,
    @SerializedName("created_at") val createdAt: String?,
    val age: Int?
)

data class LoginRequest(
    val email: String,
    val password: String
)

// NUEVO (login con Google): body de POST /auth/google. Un único endpoint
// sirve para login y registro (ver GoogleAuthHelper + FitrackAPI.py).
data class GoogleAuthRequest(
    @SerializedName("id_token") val idToken: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null,
    val sex: String? = null,
    @SerializedName("height_cm") val heightCm: Int? = null,
    @SerializedName("weight_kg") val weightKg: Double? = null,
    @SerializedName("daily_step_goal") val dailyStepGoal: Int = 10000
)

/** Partial profile update (PATCH). Null fields are omitted by Gson,
 *  so only the values you actually set get sent to the server. */
data class ProfileUpdateRequest(
    val name: String? = null,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null,
    val sex: String? = null,
    @SerializedName("height_cm") val heightCm: Int? = null,
    @SerializedName("weight_kg") val weightKg: Double? = null,
    @SerializedName("daily_step_goal") val dailyStepGoal: Int? = null
)

data class IdResponse(
    val id: Int
)

data class ErrorResponse(
    val error: String
)

// ===================== Daily activity =====================

data class DailyActivity(
    @SerializedName("activity_id") val activityId: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("activity_date") val activityDate: String,
    val steps: Int = 0,
    @SerializedName("calories_burned") val caloriesBurned: Int = 0,
    @SerializedName("sleep_hours") val sleepHours: Double? = null
)

// ===================== Weight history =====================

data class WeightLog(
    @SerializedName("weight_log_id") val weightLogId: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("weight_kg") val weightKg: Double,
    @SerializedName("recorded_on") val recordedOn: String
)

// NUEVO: body para POST /users/{id}/weight-history (user_id va en la URL, no en el body)
data class WeightLogRequest(
    @SerializedName("weight_kg") val weightKg: Double,
    @SerializedName("recorded_on") val recordedOn: String
)

// ===================== Workout types =====================

data class WorkoutType(
    @SerializedName("workout_type_id") val workoutTypeId: Int,
    val code: String,
    val name: String
)

// ===================== Workouts =====================

/** Read model. Also reused for the nested detail on create. */
data class Workout(
    @SerializedName("workout_id") val workoutId: Int = 0,
    @SerializedName("started_at") val startedAt: String,
    @SerializedName("duration_seconds") val durationSeconds: Int,
    @SerializedName("avg_heart_rate") val avgHeartRate: Int? = null,
    val steps: Int = 0,
    @SerializedName("calories_burned") val caloriesBurned: Int = 0,
    val notes: String? = null,
    @SerializedName("is_personal_record") val isPersonalRecord: Boolean = false,
    @SerializedName("pr_exercise") val prExercise: String? = null,
    @SerializedName("pr_result") val prResult: String? = null,
    @SerializedName("type_code") val typeCode: String = "",
    @SerializedName("type_name") val typeName: String = "",
    val exercises: List<WorkoutExercise> = emptyList(),
    val segments: List<WorkoutSegment> = emptyList()
)

data class WorkoutExercise(
    @SerializedName("exercise_id") val exerciseId: Int? = null,
    val position: Int = 1,
    val name: String,
    val sets: Int = 0,
    val reps: Int = 0,
    @SerializedName("weight_kg") val weightKg: Double = 0.0
)

data class WorkoutSegment(
    @SerializedName("segment_id") val segmentId: Int? = null,
    val position: Int = 1,
    @SerializedName("duration_seconds") val durationSeconds: Int = 0,
    @SerializedName("distance_m") val distanceM: Int? = null,
    val note: String? = null
)

/** Create model: what the app POSTs to save a workout, detail included. */
data class WorkoutRequest(
    @SerializedName("type_code") val typeCode: String,
    @SerializedName("started_at") val startedAt: String,
    @SerializedName("duration_seconds") val durationSeconds: Int,
    @SerializedName("avg_heart_rate") val avgHeartRate: Int? = null,
    val steps: Int = 0,
    @SerializedName("calories_burned") val caloriesBurned: Int = 0,
    val notes: String? = null,
    @SerializedName("is_personal_record") val isPersonalRecord: Boolean = false,
    @SerializedName("pr_exercise") val prExercise: String? = null,
    @SerializedName("pr_result") val prResult: String? = null,
    val exercises: List<WorkoutExercise> = emptyList(),
    val segments: List<WorkoutSegment> = emptyList()
)
