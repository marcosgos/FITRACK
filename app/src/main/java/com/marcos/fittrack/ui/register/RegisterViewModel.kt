package com.marcos.fittrack.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.RegistroRequest
import com.marcos.fittrack.data.repository.UsuarioRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed class EstadoRegistro {
    object Inicial : EstadoRegistro()
    object Cargando : EstadoRegistro()
    data class Exito(val idUsuario: Int, val nombre: String) : EstadoRegistro()
    data class Error(val mensaje: String) : EstadoRegistro()
}

class RegisterViewModel : ViewModel() {

    private val repository = UsuarioRepository()

    private val _estadoRegistro = MutableLiveData<EstadoRegistro>(EstadoRegistro.Inicial)
    val estadoRegistro: LiveData<EstadoRegistro> = _estadoRegistro

    fun registrar(nombre: String, fechaNacimiento: String, correo: String, contrasena: String) {
        if (nombre.isBlank() || fechaNacimiento.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _estadoRegistro.value = EstadoRegistro.Error("Rellena todos los campos")
            return
        }

        val edad = calcularEdad(fechaNacimiento)
        if (edad == null) {
            _estadoRegistro.value = EstadoRegistro.Error("Fecha no válida (usa dd/mm/aaaa)")
            return
        }

        _estadoRegistro.value = EstadoRegistro.Cargando

        val datos = RegistroRequest(
            nombre = nombre,
            edad = edad,
            peso = 0.0, // se pedirá más adelante, ej. en un perfil
            correo = correo,
            contrasena = contrasena
        )

        repository.registrar(
            datos = datos,
            alExito = { id -> _estadoRegistro.value = EstadoRegistro.Exito(id, nombre) },
            alError = { mensaje -> _estadoRegistro.value = EstadoRegistro.Error(mensaje) }
        )
    }

    private fun calcularEdad(fechaTexto: String): Int? {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.isLenient = false
            val fechaNacimiento = formato.parse(fechaTexto) ?: return null

            val nacimiento = Calendar.getInstance().apply { time = fechaNacimiento }
            val hoy = Calendar.getInstance()

            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad--
            }
            if (edad < 0 || edad > 120) null else edad
        } catch (e: Exception) {
            null
        }
    }
}