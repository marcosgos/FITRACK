package com.marcos.fittrack.ui.entrenamiento

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.WorkoutRequest
import com.marcos.fittrack.data.repository.UserRepository

sealed class EstadoGuardado {
    object Cargando : EstadoGuardado()
    object Exito : EstadoGuardado()
    data class Error(val mensaje: String) : EstadoGuardado()
}

class NuevoEntrenamientoViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _estado = MutableLiveData<EstadoGuardado>()
    val estado: LiveData<EstadoGuardado> = _estado

    // FIX: la versión anterior resolvía el tipo de entrenamiento en el cliente:
    // pedía la lista de tipos en el init{} (async) y buscaba por nombre visible
    // ("Carrera", "Fuerza"...). Eso daba "tipo no existe" si se guardaba antes de
    // que llegara esa respuesta (race condition), y dejó de funcionar del todo al
    // migrar la BD/API a inglés porque el nombre guardado pasó a ser "Running"/
    // "Strength" y ya no coincidía nunca con el texto en español.
    // Ahora se manda directamente el `type_code` estable (ver
    // NuevoEntrenamientoActivity.codigoTipo()) y es la API la que resuelve el
    // workout_type_id contra ese code, sin depender de ninguna carga previa.

    fun guardar(userId: Int, datos: WorkoutRequest) {
        _estado.value = EstadoGuardado.Cargando
        repository.saveWorkout(
            userId = userId,
            data = datos,
            onSuccess = { _estado.value = EstadoGuardado.Exito },
            onError = { mensaje -> _estado.value = EstadoGuardado.Error(mensaje) }
        )
    }
}