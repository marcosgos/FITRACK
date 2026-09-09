package com.marcos.fittrack.ui.perfil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcos.fittrack.data.model.ProfileUpdateRequest
import com.marcos.fittrack.data.model.User
import com.marcos.fittrack.data.model.WeightLogRequest
import com.marcos.fittrack.data.repository.UserRepository
import com.marcos.fittrack.data.util.calcularEdad
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Campo al que asociar un error del formulario; null = error general (sin campo concreto). */
enum class CampoDatosPersonales { NOMBRE, FECHA_NACIMIENTO }

sealed class EstadoDatosPersonales {
    object Cargando : EstadoDatosPersonales()
    data class Cargado(val usuario: User) : EstadoDatosPersonales()
    object Guardando : EstadoDatosPersonales()
    data class Guardado(val esOnboarding: Boolean) : EstadoDatosPersonales()
    data class Error(val mensaje: String, val campo: CampoDatosPersonales? = null) : EstadoDatosPersonales()
}

const val EDAD_MINIMA = 18

class DatosPersonalesViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _estado = MutableLiveData<EstadoDatosPersonales>()
    val estado: LiveData<EstadoDatosPersonales> = _estado

    fun cargar(userId: Int) {
        _estado.value = EstadoDatosPersonales.Cargando
        repository.getUser(
            userId,
            onSuccess = { usuario -> _estado.value = EstadoDatosPersonales.Cargado(usuario) },
            onError = { mensaje -> _estado.value = EstadoDatosPersonales.Error(mensaje) }
        )
    }

    fun guardar(
        userId: Int,
        nombre: String,
        fechaNacimiento: String?,
        sexo: String?,
        alturaCm: Int?,
        pesoKg: Double?,
        objetivoPasos: Int,
        esOnboarding: Boolean
    ) {
        if (nombre.isBlank()) {
            _estado.value = EstadoDatosPersonales.Error("Introduce tu nombre", CampoDatosPersonales.NOMBRE)
            return
        }
        if (fechaNacimiento.isNullOrBlank()) {
            _estado.value = EstadoDatosPersonales.Error(
                "Introduce tu fecha de nacimiento",
                CampoDatosPersonales.FECHA_NACIMIENTO
            )
            return
        }
        val edad = calcularEdad(fechaNacimiento)
        if (edad == null || edad < EDAD_MINIMA) {
            _estado.value = EstadoDatosPersonales.Error(
                "Debes tener al menos $EDAD_MINIMA años para usar FitTrack+",
                CampoDatosPersonales.FECHA_NACIMIENTO
            )
            return
        }

        _estado.value = EstadoDatosPersonales.Guardando

        val datos = ProfileUpdateRequest(
            name = nombre,
            dateOfBirth = fechaNacimiento,
            sex = sexo,
            heightCm = alturaCm,
            weightKg = pesoKg,
            dailyStepGoal = objetivoPasos
        )

        repository.updateProfile(
            userId = userId,
            data = datos,
            onSuccess = {
                if (pesoKg != null) {
                    // El peso también queda registrado en el histórico del día
                    // (best-effort: si falla, el perfil ya se guardó igualmente).
                    val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    repository.addWeightLog(
                        userId = userId,
                        data = WeightLogRequest(weightKg = pesoKg, recordedOn = hoy),
                        onSuccess = { _estado.value = EstadoDatosPersonales.Guardado(esOnboarding) },
                        onError = { _estado.value = EstadoDatosPersonales.Guardado(esOnboarding) }
                    )
                } else {
                    _estado.value = EstadoDatosPersonales.Guardado(esOnboarding)
                }
            },
            onError = { mensaje -> _estado.value = EstadoDatosPersonales.Error(mensaje) }
        )
    }
}
