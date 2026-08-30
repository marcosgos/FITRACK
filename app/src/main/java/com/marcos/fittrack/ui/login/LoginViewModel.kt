package com.marcos.fittrack.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.User
import com.marcos.fittrack.data.repository.UserRepository

sealed class EstadoLogin {
    object Inicial : EstadoLogin()
    object Cargando : EstadoLogin()
    data class Exito(val usuario: User) : EstadoLogin()
    data class Error(val mensaje: String) : EstadoLogin()
}

class LoginViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _estadoLogin = MutableLiveData<EstadoLogin>(EstadoLogin.Inicial)
    val estadoLogin: LiveData<EstadoLogin> = _estadoLogin

    fun iniciarSesion(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _estadoLogin.value = EstadoLogin.Error("Rellena correo y contraseña")
            return
        }

        _estadoLogin.value = EstadoLogin.Cargando

        repository.login(
            email = correo,
            password = contrasena,
            onSuccess = { usuario -> _estadoLogin.value = EstadoLogin.Exito(usuario) },
            onError = { mensaje -> _estadoLogin.value = EstadoLogin.Error(mensaje) }
        )
    }

    // NUEVO (login con Google): la Activity ya obtuvo el idToken vía
    // GoogleAuthHelper antes de llamar aquí.
    fun iniciarSesionConGoogle(idToken: String) {
        _estadoLogin.value = EstadoLogin.Cargando

        repository.loginWithGoogle(
            idToken = idToken,
            onSuccess = { usuario -> _estadoLogin.value = EstadoLogin.Exito(usuario) },
            onError = { mensaje -> _estadoLogin.value = EstadoLogin.Error(mensaje) }
        )
    }
}
