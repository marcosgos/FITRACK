package com.marcos.fittrack.ui.entrenamiento

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.WorkoutRequest
import com.marcos.fittrack.data.repository.UserRepository

sealed class EstadoGuardado {
    object Cargando : EstadoGuardado()
    data class Exito(val idEntrenamiento: Int) : EstadoGuardado()
    data class Error(val mensaje: String) : EstadoGuardado()
}

class NuevoEntrenamientoViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _estadoGuardado = MutableLiveData<EstadoGuardado>()
    val estadoGuardado: LiveData<EstadoGuardado> = _estadoGuardado

    // FIX: la versión anterior resolvía el tipo de entrenamiento en el cliente:
    // pedía la lista de tipos en el init{} (async) y buscaba por nombre visible
    // ("Carrera", "Fuerza"...). Eso daba "tipo no existe" si se guardaba antes de
    // que llegara esa respuesta (race condition), y dejó de funcionar del todo al
    // migrar la BD/API a inglés porque el nombre guardado pasó a ser "Running"/
    // "Strength" y ya no coincidía nunca con el texto en español.
    // Ahora se manda directamente el `type_code` estable (ver
    // NuevoEntrenamientoActivity.codigoTipo()) y es la API la que resuelve el
    // workout_type_id contra ese code, sin depender de ninguna carga previa.
    fun guardarEntrenamiento(idUsuario: Int, request: WorkoutRequest) {
        if (idUsuario == -1) {
            _estadoGuardado.value = EstadoGuardado.Error("No se identificó al usuario")
            return
        }
        if (request.durationSeconds <= 0) {
            _estadoGuardado.value = EstadoGuardado.Error("La duración debe ser mayor que 0")
            return
        }

        _estadoGuardado.value = EstadoGuardado.Cargando

        repository.saveWorkout(
            userId = idUsuario,
            data = request,
            onSuccess = { id -> _estadoGuardado.value = EstadoGuardado.Exito(id) },
            onError = { mensaje -> _estadoGuardado.value = EstadoGuardado.Error(mensaje) }
        )
    }
}
