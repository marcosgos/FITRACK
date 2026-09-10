package com.marcos.fittrack.service

import android.app.*
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.marcos.fittrack.R
import com.marcos.fittrack.data.util.PasosRepositorio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContadorPasosService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var sensorPasos: Sensor? = null
    private lateinit var prefs: android.content.SharedPreferences

    companion object {
        private const val CANAL_ID = "contador_pasos_canal"
        private const val NOTIFICACION_ID = 1001
        private const val PREFS_NAME = "contador_pasos_prefs"
        private const val CLAVE_BASE = "pasos_base"
        private const val CLAVE_FECHA = "fecha_base"
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorPasos = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        crearCanalNotificacion()
        startForeground(NOTIFICACION_ID, construirNotificacion(0))

        sensorPasos?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Si Android mata el proceso por falta de memoria, intenta recrear el servicio
        return START_STICKY
    }

    override fun onSensorChanged(evento: SensorEvent) {
        val totalDispositivo = evento.values[0]
        val hoy = obtenerFechaHoy()
        val fechaGuardada = prefs.getString(CLAVE_FECHA, null)

        // Si ha cambiado el día, la base se reinicia al valor actual (pasos de hoy = 0)
        if (fechaGuardada != hoy) {
            prefs.edit()
                .putFloat(CLAVE_BASE, totalDispositivo)
                .putString(CLAVE_FECHA, hoy)
                .apply()
        }

        val base = prefs.getFloat(CLAVE_BASE, totalDispositivo)
        val pasosHoy = (totalDispositivo - base).toInt().coerceAtLeast(0)

        PasosRepositorio.actualizar(pasosHoy)
        actualizarNotificacion(pasosHoy)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    private fun obtenerFechaHoy(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID, "Contador de pasos", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    private fun construirNotificacion(pasos: Int): Notification {
        return NotificationCompat.Builder(this, CANAL_ID)
            .setContentTitle("FitTrack")
            .setContentText("$pasos pasos hoy")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    private fun actualizarNotificacion(pasos: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICACION_ID, construirNotificacion(pasos))
    }
}