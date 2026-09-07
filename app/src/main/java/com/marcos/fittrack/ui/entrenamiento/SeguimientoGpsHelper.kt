package com.marcos.fittrack.ui.entrenamiento

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import com.google.android.gms.location.*

class SeguimientoGpsHelper(private val context: Context, private val contarPasos: Boolean) {

    private val clienteUbicacion = LocationServices.getFusedLocationProviderClient(context)
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorPasos = if (contarPasos) sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) else null

    private var ubicacionAnterior: Location? = null
    private var distanciaTotalMetros = 0.0
    private var velocidadMaximaMs = 0f
    private var altitudInicial: Double? = null
    private var desnivelAcumuladoMetros = 0.0
    private var pasosIniciales: Float? = null
    private var pasosActuales = 0

    private var callbackUbicacion: LocationCallback? = null
    private var listenerPasos: SensorEventListener? = null

    fun iniciar(
        tiempoTranscurridoSegundosProvider: () -> Int,
        alActualizar: (distanciaMetros: Int, velMediaKmh: Double, velMaxKmh: Double, desnivel: Int, pasos: Int) -> Unit
    ) {
        val solicitud = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L).build()

        callbackUbicacion = object : LocationCallback() {
            override fun onLocationResult(resultado: LocationResult) {
                val ubicacion = resultado.lastLocation ?: return

                ubicacionAnterior?.let { anterior -> distanciaTotalMetros += anterior.distanceTo(ubicacion) }
                ubicacionAnterior = ubicacion

                if (ubicacion.hasSpeed() && ubicacion.speed > velocidadMaximaMs) {
                    velocidadMaximaMs = ubicacion.speed
                }

                if (ubicacion.hasAltitude()) {
                    val inicial = altitudInicial
                    if (inicial == null) {
                        altitudInicial = ubicacion.altitude
                    } else if (ubicacion.altitude > inicial) {
                        desnivelAcumuladoMetros += (ubicacion.altitude - inicial)
                        altitudInicial = ubicacion.altitude
                    } else {
                        altitudInicial = ubicacion.altitude
                    }
                }

                val segundos = tiempoTranscurridoSegundosProvider()
                val distanciaKm = distanciaTotalMetros / 1000.0
                val velMediaKmh = if (segundos > 0) (distanciaKm / (segundos / 3600.0)) else 0.0

                alActualizar(
                    distanciaTotalMetros.toInt(),
                    velMediaKmh,
                    velocidadMaximaMs * 3.6,
                    desnivelAcumuladoMetros.toInt(),
                    pasosActuales
                )
            }
        }

        @SuppressLint("MissingPermission")
        try {
            clienteUbicacion.requestLocationUpdates(solicitud, callbackUbicacion!!, context.mainLooper)
        } catch (e: SecurityException) { /* permiso gestionado por la Activity antes de llamar */ }

        if (sensorPasos != null) {
            listenerPasos = object : SensorEventListener {
                override fun onSensorChanged(evento: SensorEvent) {
                    val total = evento.values[0]
                    if (pasosIniciales == null) pasosIniciales = total
                    pasosActuales = (total - (pasosIniciales ?: total)).toInt()
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(listenerPasos, sensorPasos, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun detener() {
        callbackUbicacion?.let { clienteUbicacion.removeLocationUpdates(it) }
        listenerPasos?.let { sensorManager.unregisterListener(it) }
    }
}