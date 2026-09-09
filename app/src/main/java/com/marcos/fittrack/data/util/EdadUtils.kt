package com.marcos.fittrack.data.util

import java.util.Calendar
import java.util.Locale

/** Deriva la edad a partir de una fecha de nacimiento "yyyy-MM-dd" (o null/inválida -> null). */
fun calcularEdad(fechaNacimientoIso: String?): Int? {
    if (fechaNacimientoIso.isNullOrBlank()) return null
    val partes = fechaNacimientoIso.split("-")
    if (partes.size != 3) return null
    val anio = partes[0].toIntOrNull() ?: return null
    val mes = partes[1].toIntOrNull() ?: return null
    val dia = partes[2].toIntOrNull() ?: return null

    val hoy = Calendar.getInstance(Locale.getDefault())

    // Compara (mes, día) en vez de DAY_OF_YEAR: con DAY_OF_YEAR un nacimiento
    // en/después del 29 de febrero se desplaza un día en años no bisiestos y
    // puede dar una edad distinta a la que calcula el backend (compute_age en
    // FitrackAPI.py, que sí compara mes/día), justo en el límite de los 18 años.
    var edad = hoy.get(Calendar.YEAR) - anio
    val cumpleAunNoLlega = (hoy.get(Calendar.MONTH) + 1 < mes) ||
        (hoy.get(Calendar.MONTH) + 1 == mes && hoy.get(Calendar.DAY_OF_MONTH) < dia)
    if (cumpleAunNoLlega) edad--
    return edad
}
