package com.marcos.fittrack.ui.entrenamiento

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.EntrenamientoRequest
import com.marcos.fittrack.data.model.TipoEntrenamientoApi
import com.marcos.fittrack.data.repository.UsuarioRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class EstadoGuardado {
    object Cargando : EstadoGuardado()
    data class Exito(val idEntrenamiento: Int) : EstadoGuardado()
    data class Error(val mensaje: String) : EstadoGuardado()
}

class NuevoEntrenamientoViewModel : ViewModel() {

    private val repository = UsuarioRepository()

    private var tiposDisponibles: List<TipoEntrenamientoApi> = emptyList()

    private val _estadoGuardado = MutableLiveData<EstadoGuardado>()
    val estadoGuardado: LiveData<EstadoGuardado> = _estadoGuardado

    init {
        repository.obtenerTiposEntrenamiento(
            alExito = { lista -> tiposDisponibles = lista },
            alError = { /* si falla, guardarEntrenamiento avisará al no encontrar el tipo */ }
        )
    }

    fun guardarEntrenamiento(idUsuario: Int, nombreTipo: String, duracionMinutos: Int) {
        if (idUsuario == -1) {
            _estadoGuardado.value = EstadoGuardado.Error("No se identificó al usuario")
            return
        }
        if (duracionMinutos <= 0) {
            _estadoGuardado.value = EstadoGuardado.Error("La duración debe ser mayor que 0")
            return
        }

        val tipo = tiposDisponibles.find { it.nombre.equals(nombreTipo, ignoreCase = true) }
        if (tipo == null) {
            _estadoGuardado.value = EstadoGuardado.Error(
                "El tipo '$nombreTipo' no existe en la base de datos"
            )
            return
        }

        _estadoGuardado.value = EstadoGuardado.Cargando

        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaActual = formato.format(Date())

        val datos = EntrenamientoRequest(
            id_tipo = tipo.id_tipo,
            fecha_inicio = fechaActual,
            duracion_segundos = duracionMinutos * 60
        )

        repository.guardarEntrenamiento(
            idUsuario = idUsuario,
            datos = datos,
            alExito = { id -> _estadoGuardado.value = EstadoGuardado.Exito(id) },
            alError = { mensaje -> _estadoGuardado.value = EstadoGuardado.Error(mensaje) }
        )
    }
}