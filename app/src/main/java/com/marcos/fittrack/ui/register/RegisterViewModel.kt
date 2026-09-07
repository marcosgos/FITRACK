package com.marcos.fittrack.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.RegisterRequest
import com.marcos.fittrack.data.repository.UserRepository

/** Campo al que asociar un error del formulario; null = error general (sin campo concreto). */
enum class CampoRegistro { NOMBRE, CORREO, CONTRASENA }

sealed class EstadoRegistro {
    object Inicial : EstadoRegistro()
    object Cargando : EstadoRegistro()
    data class Exito(val idUsuario: Int, val nombre: String) : EstadoRegistro()
    data class Error(val mensaje: String, val campo: CampoRegistro? = null) : EstadoRegistro()
}

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _estadoRegistro = MutableLiveData<EstadoRegistro>(EstadoRegistro.Inicial)
    val estadoRegistro: LiveData<EstadoRegistro> = _estadoRegistro

    // La fecha de nacimiento, el peso y demás datos se piden luego en la
    // pantalla de perfil, no aquí.
    fun registrar(nombre: String, correo: String, contrasena: String) {
        if (nombre.isBlank()) {
            _estadoRegistro.value = EstadoRegistro.Error("Introduce tu nombre", CampoRegistro.NOMBRE)
            return
        }
        if (correo.isBlank()) {
            _estadoRegistro.value = EstadoRegistro.Error("Introduce tu correo", CampoRegistro.CORREO)
            return
        }
        if (contrasena.length < 8) {
            _estadoRegistro.value = EstadoRegistro.Error(
                "La contraseña debe tener al menos 8 caracteres",
                CampoRegistro.CONTRASENA
            )
            return
        }

        _estadoRegistro.value = EstadoRegistro.Cargando

        val datos = RegisterRequest(
            name = nombre,
            email = correo,
            password = contrasena
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
}
