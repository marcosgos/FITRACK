package com.marcos.fittrack.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.DailyActivity
import com.marcos.fittrack.data.model.Workout
import com.marcos.fittrack.data.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HomeUiState(
    val entrenamientosSemana: List<Workout>,
    val actividadHoy: DailyActivity?
)

sealed class EstadoHome {
    object Cargando : EstadoHome()
    data class Exito(val datos: HomeUiState) : EstadoHome()
    data class Error(val mensaje: String) : EstadoHome()
}

class HomeViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _estadoHome = MutableLiveData<EstadoHome>()
    val estadoHome: LiveData<EstadoHome> = _estadoHome

    private var entrenamientosCache: List<Workout>? = null
    private var actividadCache: List<DailyActivity>? = null

    fun cargarDatos(idUsuario: Int) {
        _estadoHome.value = EstadoHome.Cargando
        entrenamientosCache = null
        actividadCache = null

        repository.getWorkouts(
            userId = idUsuario,
            onSuccess = { lista ->
                entrenamientosCache = lista
                intentarCompletar()
            },
            onError = { mensaje -> _estadoHome.value = EstadoHome.Error(mensaje) }
        )

        repository.getActivity(
            userId = idUsuario,
            onSuccess = { lista ->
                actividadCache = lista
                intentarCompletar()
            },
            onError = { mensaje -> _estadoHome.value = EstadoHome.Error(mensaje) }
        )
    }

    private fun intentarCompletar() {
        val entrenamientos = entrenamientosCache
        val actividad = actividadCache
        if (entrenamientos == null || actividad == null) return // esperamos a que lleguen ambas

        val entrenamientosSemana = entrenamientos.filter { esDeUltimos7Dias(it.startedAt) }
        val actividadHoy = actividad.maxByOrNull { it.activityDate } // la fecha más reciente

        _estadoHome.value = EstadoHome.Exito(
            HomeUiState(
                entrenamientosSemana = entrenamientosSemana,
                actividadHoy = actividadHoy
            )
        )
    }

    private fun esDeUltimos7Dias(fechaTexto: String): Boolean {
        return try {
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fecha = formato.parse(fechaTexto.take(19)) ?: return false

            val limite = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time
            fecha.after(limite)
        } catch (e: Exception) {
            false
        }
    }
}
