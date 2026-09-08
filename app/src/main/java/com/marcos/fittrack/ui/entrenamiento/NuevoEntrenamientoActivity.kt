package com.marcos.fittrack.ui.entrenamiento

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.marcos.fittrack.R
import com.marcos.fittrack.data.model.WorkoutRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.text.Editable

class NuevoEntrenamientoActivity : AppCompatActivity() {

    private enum class Deporte(val typeCode: String) {
        CARRERA("running"), CICLISMO("cycling"), NATACION("swimming")
    }

    private val viewModel: NuevoEntrenamientoViewModel by viewModels()
    private var deporteSeleccionado = Deporte.CARRERA
    private var userId: Int = -1

    // ---- Carrera ----
    private lateinit var seguimientoCarrera: SeguimientoGpsHelper
    private var segundosCarrera = 0
    private var activoCarrera = false
    private val handlerCarrera = Handler(Looper.getMainLooper())
    private lateinit var runnableCarrera: Runnable
    private var distanciaMCarrera = 0
    private var velMediaCarrera = 0.0
    private var velMaxCarrera = 0.0
    private var desnivelCarrera = 0
    private var pasosCarrera = 0

    // ---- Ciclismo ----
    private lateinit var seguimientoCiclismo: SeguimientoGpsHelper
    private var segundosCiclismo = 0
    private var activoCiclismo = false
    private val handlerCiclismo = Handler(Looper.getMainLooper())
    private lateinit var runnableCiclismo: Runnable
    private var distanciaMCiclismo = 0
    private var velMediaCiclismo = 0.0
    private var velMaxCiclismo = 0.0
    private var desnivelCiclismo = 0

    // ---- Natacion ----
    private var segundosNatacion = 0
    private var activoNatacion = false
    private val handlerNatacion = Handler(Looper.getMainLooper())
    private lateinit var runnableNatacion: Runnable
    private var estiloSeleccionado = "freestyle"

    private val lanzadorPermisosCarrera = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos -> if (permisos.values.all { it }) iniciarCronometroCarrera() else avisarPermisoDenegado() }

    private val lanzadorPermisosCiclismo = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos -> if (permisos.values.all { it }) iniciarCronometroCiclismo() else avisarPermisoDenegado() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_entrenamiento)

        userId = intent.getIntExtra("idUsuario", -1)

        findViewById<View>(R.id.btnVolver).setOnClickListener { finish() }

        configurarSelectorDeporte()
        configurarCarrera()
        configurarCiclismo()
        configurarNatacion()
        configurarGuardar()
        observarEstado()

        seleccionarDeporte(Deporte.CARRERA)
    }

    // ---------- SELECTOR ----------

    private fun configurarSelectorDeporte() {
        findViewById<Button>(R.id.chipCarrera).setOnClickListener { seleccionarDeporte(Deporte.CARRERA) }
        findViewById<Button>(R.id.chipCiclismo).setOnClickListener { seleccionarDeporte(Deporte.CICLISMO) }
        findViewById<Button>(R.id.chipNatacion).setOnClickListener { seleccionarDeporte(Deporte.NATACION) }
    }

    private fun seleccionarDeporte(deporte: Deporte) {
        deporteSeleccionado = deporte

        val mapaChips = mapOf(
            Deporte.CARRERA to R.id.chipCarrera,
            Deporte.CICLISMO to R.id.chipCiclismo,
            Deporte.NATACION to R.id.chipNatacion
        )
        mapaChips.forEach { (d, idBoton) ->
            findViewById<Button>(idBoton).backgroundTintList = android.content.res.ColorStateList.valueOf(
                Color.parseColor(if (d == deporte) "#E31E24" else "#2A2A2A")
            )
        }

        findViewById<View>(R.id.groupCarrera).visibility = if (deporte == Deporte.CARRERA) View.VISIBLE else View.GONE
        findViewById<View>(R.id.groupCiclismo).visibility = if (deporte == Deporte.CICLISMO) View.VISIBLE else View.GONE
        findViewById<View>(R.id.groupNatacion).visibility = if (deporte == Deporte.NATACION) View.VISIBLE else View.GONE
    }

    // ---------- CARRERA ----------

    private fun configurarCarrera() {
        seguimientoCarrera = SeguimientoGpsHelper(this, contarPasos = true)

        findViewById<Button>(R.id.btnIniciarCarrera).setOnClickListener { pedirPermisosCarrera() }
        findViewById<Button>(R.id.btnPausarCarrera).setOnClickListener { pausarCarrera() }
        findViewById<Button>(R.id.btnFinalizarCarrera).setOnClickListener { finalizarCarrera() }

        runnableCarrera = object : Runnable {
            override fun run() {
                if (activoCarrera) {
                    segundosCarrera++
                    findViewById<TextView>(R.id.tvTiempoCarrera).text = formatoTiempo(segundosCarrera)
                    handlerCarrera.postDelayed(this, 1000)
                }
            }
        }
    }

    private fun pedirPermisosCarrera() {
        val permisos = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permisos.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            lanzadorPermisosCarrera.launch(permisos)
        } else {
            iniciarCronometroCarrera()
        }
    }

    private fun iniciarCronometroCarrera() {
        activoCarrera = true
        findViewById<TextView>(R.id.tvEstadoCarrera).text = "EN MARCHA"
        findViewById<TextView>(R.id.tvEstadoCarrera).setTextColor(Color.parseColor("#E31E24"))
        findViewById<View>(R.id.btnIniciarCarrera).visibility = View.GONE
        findViewById<View>(R.id.rowPausarFinalizarCarrera).visibility = View.VISIBLE
        handlerCarrera.postDelayed(runnableCarrera, 1000)

        seguimientoCarrera.iniciar(
            tiempoTranscurridoSegundosProvider = { segundosCarrera }
        ) { distanciaM, velMedia, velMax, desnivel, pasos ->
            distanciaMCarrera = distanciaM
            velMediaCarrera = velMedia
            velMaxCarrera = velMax
            desnivelCarrera = desnivel
            pasosCarrera = pasos

            findViewById<TextView>(R.id.tvDistanciaCarrera).text = String.format("%.2f km", distanciaM / 1000.0)
            findViewById<TextView>(R.id.tvVelMediaCarrera).text = String.format("%.1f km/h", velMedia)
            findViewById<TextView>(R.id.tvDesnivelCarrera).text = "$desnivel m"
            findViewById<TextView>(R.id.tvPasosCarrera).text = pasos.toString()
            findViewById<TextView>(R.id.tvRitmoCarrera).text = calcularRitmo(velMedia)
        }
    }

    private fun pausarCarrera() {
        activoCarrera = !activoCarrera
        val boton = findViewById<Button>(R.id.btnPausarCarrera)
        if (activoCarrera) {
            boton.text = "Pausar"
            findViewById<TextView>(R.id.tvEstadoCarrera).text = "EN MARCHA"
            handlerCarrera.postDelayed(runnableCarrera, 1000)
        } else {
            boton.text = "Reanudar"
            findViewById<TextView>(R.id.tvEstadoCarrera).text = "EN PAUSA"
        }
    }

    private fun finalizarCarrera() {
        activoCarrera = false
        handlerCarrera.removeCallbacks(runnableCarrera)
        seguimientoCarrera.detener()
        findViewById<TextView>(R.id.tvEstadoCarrera).text = "FINALIZADO"
        findViewById<TextView>(R.id.tvEstadoCarrera).setTextColor(Color.parseColor("#2ECC71"))
        findViewById<View>(R.id.rowPausarFinalizarCarrera).visibility = View.GONE
    }

    // ---------- CICLISMO ----------

    private fun configurarCiclismo() {
        seguimientoCiclismo = SeguimientoGpsHelper(this, contarPasos = false)

        findViewById<Button>(R.id.btnIniciarCiclismo).setOnClickListener { pedirPermisosCiclismo() }
        findViewById<Button>(R.id.btnPausarCiclismo).setOnClickListener { pausarCiclismo() }
        findViewById<Button>(R.id.btnFinalizarCiclismo).setOnClickListener { finalizarCiclismo() }

        runnableCiclismo = object : Runnable {
            override fun run() {
                if (activoCiclismo) {
                    segundosCiclismo++
                    findViewById<TextView>(R.id.tvTiempoCiclismo).text = formatoTiempo(segundosCiclismo)
                    handlerCiclismo.postDelayed(this, 1000)
                }
            }
        }
    }

    private fun pedirPermisosCiclismo() {
        val permisos = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permisos.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            lanzadorPermisosCiclismo.launch(permisos)
        } else {
            iniciarCronometroCiclismo()
        }
    }

    private fun iniciarCronometroCiclismo() {
        activoCiclismo = true
        findViewById<TextView>(R.id.tvEstadoCiclismo).text = "EN MARCHA"
        findViewById<TextView>(R.id.tvEstadoCiclismo).setTextColor(Color.parseColor("#E31E24"))
        findViewById<View>(R.id.btnIniciarCiclismo).visibility = View.GONE
        findViewById<View>(R.id.rowPausarFinalizarCiclismo).visibility = View.VISIBLE
        handlerCiclismo.postDelayed(runnableCiclismo, 1000)

        seguimientoCiclismo.iniciar(
            tiempoTranscurridoSegundosProvider = { segundosCiclismo }
        ) { distanciaM, velMedia, velMax, desnivel, _ ->
            distanciaMCiclismo = distanciaM
            velMediaCiclismo = velMedia
            velMaxCiclismo = velMax
            desnivelCiclismo = desnivel

            findViewById<TextView>(R.id.tvDistanciaCiclismo).text = String.format("%.2f km", distanciaM / 1000.0)
            findViewById<TextView>(R.id.tvVelMediaCiclismo).text = String.format("%.1f km/h", velMedia)
            findViewById<TextView>(R.id.tvVelMaximaCiclismo).text = String.format("%.1f km/h", velMax)
            findViewById<TextView>(R.id.tvDesnivelCiclismo).text = "$desnivel m"
        }
    }

    private fun pausarCiclismo() {
        activoCiclismo = !activoCiclismo
        val boton = findViewById<Button>(R.id.btnPausarCiclismo)
        if (activoCiclismo) {
            boton.text = "Pausar"
            findViewById<TextView>(R.id.tvEstadoCiclismo).text = "EN MARCHA"
            handlerCiclismo.postDelayed(runnableCiclismo, 1000)
        } else {
            boton.text = "Reanudar"
            findViewById<TextView>(R.id.tvEstadoCiclismo).text = "EN PAUSA"
        }
    }

    private fun finalizarCiclismo() {
        activoCiclismo = false
        handlerCiclismo.removeCallbacks(runnableCiclismo)
        seguimientoCiclismo.detener()
        findViewById<TextView>(R.id.tvEstadoCiclismo).text = "FINALIZADO"
        findViewById<TextView>(R.id.tvEstadoCiclismo).setTextColor(Color.parseColor("#2ECC71"))
        findViewById<View>(R.id.rowPausarFinalizarCiclismo).visibility = View.GONE
    }

    // ---------- NATACION ----------

    private fun configurarNatacion() {
        findViewById<Button>(R.id.btnIniciarNatacion).setOnClickListener { iniciarCronometroNatacion() }
        findViewById<Button>(R.id.btnPausarNatacion).setOnClickListener { pausarNatacion() }
        findViewById<Button>(R.id.btnFinalizarNatacion).setOnClickListener { finalizarNatacion() }

        val chipsEstilo = mapOf(
            "freestyle" to R.id.chipLibreNatacion,
            "backstroke" to R.id.chipEspaldaNatacion,
            "breaststroke" to R.id.chipBrazaNatacion,
            "butterfly" to R.id.chipMariposaNatacion
        )
        chipsEstilo.forEach { (codigo, idBoton) ->
            findViewById<Button>(idBoton).setOnClickListener {
                estiloSeleccionado = codigo
                chipsEstilo.forEach { (c, id) ->
                    findViewById<Button>(id).backgroundTintList = android.content.res.ColorStateList.valueOf(
                        Color.parseColor(if (c == codigo) "#E31E24" else "#2A2A2A")
                    )
                }
            }
        }
        findViewById<Button>(R.id.chipLibreNatacion).performClick()

        val actualizarDistancia = {
            val largos = findViewById<EditText>(R.id.etLargosNatacion).text.toString().toIntOrNull() ?: 0
            val longitud = findViewById<EditText>(R.id.etLongitudPiscinaNatacion).text.toString().toDoubleOrNull() ?: 0.0
            val distancia = (largos * longitud).toInt()
            findViewById<TextView>(R.id.tvDistanciaCalculadaNatacion).text = "Distancia: $distancia m"
        }
        findViewById<EditText>(R.id.etLargosNatacion).addTextChangedListener(SimpleWatcher { actualizarDistancia() })
        findViewById<EditText>(R.id.etLongitudPiscinaNatacion).addTextChangedListener(SimpleWatcher { actualizarDistancia() })

        runnableNatacion = object : Runnable {
            override fun run() {
                if (activoNatacion) {
                    segundosNatacion++
                    findViewById<TextView>(R.id.tvTiempoNatacion).text = formatoTiempo(segundosNatacion)
                    handlerNatacion.postDelayed(this, 1000)
                }
            }
        }
    }

    private fun iniciarCronometroNatacion() {
        activoNatacion = true
        findViewById<TextView>(R.id.tvEstadoNatacion).text = "EN MARCHA"
        findViewById<TextView>(R.id.tvEstadoNatacion).setTextColor(Color.parseColor("#E31E24"))
        findViewById<View>(R.id.btnIniciarNatacion).visibility = View.GONE
        findViewById<View>(R.id.rowPausarFinalizarNatacion).visibility = View.VISIBLE
        handlerNatacion.postDelayed(runnableNatacion, 1000)
    }

    private fun pausarNatacion() {
        activoNatacion = !activoNatacion
        val boton = findViewById<Button>(R.id.btnPausarNatacion)
        if (activoNatacion) {
            boton.text = "Pausar"
            findViewById<TextView>(R.id.tvEstadoNatacion).text = "EN MARCHA"
            handlerNatacion.postDelayed(runnableNatacion, 1000)
        } else {
            boton.text = "Reanudar"
            findViewById<TextView>(R.id.tvEstadoNatacion).text = "EN PAUSA"
        }
    }

    private fun finalizarNatacion() {
        activoNatacion = false
        handlerNatacion.removeCallbacks(runnableNatacion)
        findViewById<TextView>(R.id.tvEstadoNatacion).text = "FINALIZADO"
        findViewById<TextView>(R.id.tvEstadoNatacion).setTextColor(Color.parseColor("#2ECC71"))
        findViewById<View>(R.id.rowPausarFinalizarNatacion).visibility = View.GONE
    }

    // ---------- GUARDAR ----------

    private fun configurarGuardar() {
        findViewById<Button>(R.id.btnGuardarEntrenamiento).setOnClickListener {
            if (userId == -1) {
                Toast.makeText(this, "No se identificó al usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ahora = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val solicitud = when (deporteSeleccionado) {
                Deporte.CARRERA -> WorkoutRequest(
                    typeCode = Deporte.CARRERA.typeCode,
                    startedAt = ahora,
                    durationSeconds = segundosCarrera,
                    avgHeartRate = leerFcMedia(R.id.fcManualCarrera),
                    maxHeartRate = leerFcMaxima(R.id.fcManualCarrera),
                    steps = pasosCarrera,
                    distanceM = distanciaMCarrera,
                    avgSpeedKmh = velMediaCarrera,
                    maxSpeedKmh = velMaxCarrera,
                    elevationGainM = desnivelCarrera
                )
                Deporte.CICLISMO -> WorkoutRequest(
                    typeCode = Deporte.CICLISMO.typeCode,
                    startedAt = ahora,
                    durationSeconds = segundosCiclismo,
                    avgHeartRate = leerFcMedia(R.id.fcManualCiclismo),
                    maxHeartRate = leerFcMaxima(R.id.fcManualCiclismo),
                    distanceM = distanciaMCiclismo,
                    avgSpeedKmh = velMediaCiclismo,
                    maxSpeedKmh = velMaxCiclismo,
                    elevationGainM = desnivelCiclismo
                )
                Deporte.NATACION -> {
                    val largos = findViewById<EditText>(R.id.etLargosNatacion).text.toString().toIntOrNull()
                    val longitud = findViewById<EditText>(R.id.etLongitudPiscinaNatacion).text.toString().toDoubleOrNull()
                    val distancia = if (largos != null && longitud != null) (largos * longitud).toInt() else null
                    WorkoutRequest(
                        typeCode = Deporte.NATACION.typeCode,
                        startedAt = ahora,
                        durationSeconds = segundosNatacion,
                        avgHeartRate = leerFcMedia(R.id.fcManualNatacion),
                        maxHeartRate = leerFcMaxima(R.id.fcManualNatacion),
                        distanceM = distancia,
                        swimStyle = estiloSeleccionado,
                        poolLengths = largos,
                        poolLengthM = longitud,
                        swolf = findViewById<EditText>(R.id.etSwolfNatacion).text.toString().toIntOrNull()
                    )
                }
            }

            viewModel.guardar(userId, solicitud)
        }
    }

    private fun leerFcMedia(idBloque: Int): Int? {
        val bloque = findViewById<View>(idBloque)
        return bloque.findViewById<EditText>(R.id.etFcMedia).text.toString().toIntOrNull()
    }

    private fun leerFcMaxima(idBloque: Int): Int? {
        val bloque = findViewById<View>(idBloque)
        return bloque.findViewById<EditText>(R.id.etFcMaxima).text.toString().toIntOrNull()
    }

    private fun observarEstado() {
        viewModel.estado.observe(this) { estado ->
            when (estado) {
                is EstadoGuardado.Cargando -> findViewById<Button>(R.id.btnGuardarEntrenamiento).isEnabled = false
                is EstadoGuardado.Exito -> {
                    setResult(RESULT_OK)
                    finish()
                }
                is EstadoGuardado.Error -> {
                    findViewById<Button>(R.id.btnGuardarEntrenamiento).isEnabled = true
                    Toast.makeText(this, estado.mensaje, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun avisarPermisoDenegado() {
        Toast.makeText(this, "Se necesitan permisos de ubicación", Toast.LENGTH_LONG).show()
    }

    private fun formatoTiempo(segundos: Int): String =
        String.format("%02d:%02d", segundos / 60, segundos % 60)

    private fun calcularRitmo(velocidadMediaKmh: Double): String {
        if (velocidadMediaKmh <= 0) return "--:--"
        val minPorKm = 60.0 / velocidadMediaKmh
        return String.format("%d:%02d /km", minPorKm.toInt(), ((minPorKm - minPorKm.toInt()) * 60).toInt())
    }
}

/** TextWatcher simplificado para no repetir los 3 métodos vacíos cada vez. */
class SimpleWatcher(private val alCambiar: () -> Unit) : android.text.TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { alCambiar() }
    override fun afterTextChanged(s: Editable?) {}
}