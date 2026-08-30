package com.marcos.fittrack.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.RegisterRequest
import com.marcos.fittrack.data.repository.UserRepository
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

    private val repository = UserRepository()

    private val _estadoRegistro = MutableLiveData<EstadoRegistro>(EstadoRegistro.Inicial)
    val estadoRegistro: LiveData<EstadoRegistro> = _estadoRegistro

    fun registrar(nombre: String, fechaNacimiento: String, correo: String, contrasena: String) {
        if (nombre.isBlank() || fechaNacimiento.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _estadoRegistro.value = EstadoRegistro.Error("Rellena todos los campos")
            return
        }

        val fechaIso = aFechaIso(fechaNacimiento)
        if (fechaIso == null) {
            _estadoRegistro.value = EstadoRegistro.Error("Fecha no válida (usa dd/mm/aaaa)")
            return
        }

        _estadoRegistro.value = EstadoRegistro.Cargando

        // La edad la deriva el servidor a partir de date_of_birth.
        // El peso y demás datos se piden luego en la pantalla de perfil.
        val datos = RegisterRequest(
            name = nombre,
            email = correo,
            password = contrasena,
            dateOfBirth = fechaIso
        )

        repository.register(
            data = datos,
            onSuccess = { id -> _estadoRegistro.value = EstadoRegistro.Exito(id, nombre) },
            onError = { mensaje -> _estadoRegistro.value = EstadoRegistro.Error(mensaje) }
        )
    }

    // NUEVO (login con Google): mismo endpoint que el login con Google;
    // si la cuenta no existe, la API la crea aquí mismo.
    fun registrarConGoogle(idToken: String) {
        _estadoRegistro.value = EstadoRegistro.Cargando

        repository.loginWithGoogle(
            idToken = idToken,
            onSuccess = { usuario -> _estadoRegistro.value = EstadoRegistro.Exito(usuario.userId, usuario.name) },
            onError = { mensaje -> _estadoRegistro.value = EstadoRegistro.Error(mensaje) }
        )
    }

    /** Convierte "dd/MM/yyyy" a "yyyy-MM-dd" validando que sea una fecha real
     *  y con una edad plausible. Devuelve null si no es válida. */
    private fun aFechaIso(fechaTexto: String): String? {
        return try {
            val entrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            entrada.isLenient = false
            val fecha = entrada.parse(fechaTexto) ?: return null

            val nacimiento = Calendar.getInstance().apply { time = fecha }
            val hoy = Calendar.getInstance()
            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--
            if (edad < 0 || edad > 120) return null

            val salida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            salida.format(fecha)
        } catch (e: Exception) {
            null
        }
    }
}
