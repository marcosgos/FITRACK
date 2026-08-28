package com.marcos.fittrack.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.Usuario
import com.marcos.fittrack.data.repository.UsuarioRepository

sealed class EstadoLogin {
    object Inicial : EstadoLogin()
    object Cargando : EstadoLogin()
    data class Exito(val usuario: Usuario) : EstadoLogin()
    data class Error(val mensaje: String) : EstadoLogin()
}

class LoginViewModel : ViewModel() {

    private val repository = UsuarioRepository()

    private val _estadoLogin = MutableLiveData<EstadoLogin>(EstadoLogin.Inicial)
    val estadoLogin: LiveData<EstadoLogin> = _estadoLogin

    fun iniciarSesion(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _estadoLogin.value = EstadoLogin.Error("Rellena correo y contraseña")
            return
        }

        _estadoLogin.value = EstadoLogin.Cargando

        repository.login(
            correo = correo,
            contrasena = contrasena,
            alExito = { usuario -> _estadoLogin.value = EstadoLogin.Exito(usuario) },
            alError = { mensaje -> _estadoLogin.value = EstadoLogin.Error(mensaje) }
        )
    }
}