package com.marcos.fittrack.ui.ejercicios

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.Ejercicio
import com.marcos.fittrack.data.repository.EjercicioRepository

sealed class EstadoEjercicios {
    object Cargando : EstadoEjercicios()
    object Exito : EstadoEjercicios()
    data class Error(val mensaje: String) : EstadoEjercicios()
}

class EjerciciosViewModel : ViewModel() {

    private val _estado = MutableLiveData<EstadoEjercicios>()
    val estado: LiveData<EstadoEjercicios> = _estado

    private val _listaFiltrada = MutableLiveData<List<Ejercicio>>()
    val listaFiltrada: LiveData<List<Ejercicio>> = _listaFiltrada

    private var listaCompleta: List<Ejercicio> = emptyList()

    fun cargar() {
        _estado.value = EstadoEjercicios.Cargando
        EjercicioRepository.obtenerEjercicios(
            alExito = { lista ->
                listaCompleta = lista
                _listaFiltrada.value = lista
                _estado.value = EstadoEjercicios.Exito
            },
            alError = { mensaje -> _estado.value = EstadoEjercicios.Error(mensaje) }
        )
    }

    fun filtrar(texto: String) {
        _listaFiltrada.value = if (texto.isBlank()) {
            listaCompleta
        } else {
            listaCompleta.filter { it.name.contains(texto, ignoreCase = true) }
        }
    }
}