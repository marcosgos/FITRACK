package com.marcos.fittrack.ui.entrenamiento

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.marcos.fittrack.R
import androidx.activity.viewModels

class NuevoEntrenamientoActivity : AppCompatActivity() {

    private enum class TipoEntrenamiento { CROSSFIT, CARRERA, LIBRE, MIXTO, FUERZA }

    private var tipoSeleccionado = TipoEntrenamiento.LIBRE

    // segundos acumulados en el cronómetro
    private var segundosTranscurridos = 0
    private var cronometroActivo = false
    private var cronometroFinalizado = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnableCronometro: Runnable

    private lateinit var chips: Map<TipoEntrenamiento, Button>

    private lateinit var groupDescripcionDuracion: LinearLayout
    private lateinit var groupCarrera: LinearLayout
    private lateinit var groupFuerza: LinearLayout
    private lateinit var groupPR: LinearLayout
    private lateinit var groupCamposPR: LinearLayout
    private lateinit var switchPR: Switch

    private lateinit var etDescripcion: EditText
    private lateinit var groupDuracionManual: LinearLayout
    private lateinit var etMinutosManual: EditText
    private lateinit var tvUsarCronometro: TextView

    private lateinit var groupCronometro: LinearLayout
    private lateinit var tvEstadoCronometro: TextView
    private lateinit var tvTiempoCronometro: TextView
    private lateinit var tvDuracionGuardada: TextView
    private lateinit var tvEntrenamientoGuardado: TextView
    private lateinit var rowBotonesCronometro: LinearLayout
    private lateinit var btnIniciarCronometro: Button
    private lateinit var rowPausarFinalizar: LinearLayout
    private lateinit var btnPausar: Button
    private lateinit var btnFinalizar: Button

    private lateinit var btnGuardarEntrenamiento: Button

    private lateinit var contenedorSeries: LinearLayout
    private lateinit var tvContadorSeries: TextView
    private lateinit var btnAnadirSerie: TextView
    private lateinit var contenedorEjercicios: LinearLayout
    private lateinit var btnAnadirEjercicio: TextView

    private val viewModel: NuevoEntrenamientoViewModel by viewModels()
    private var idUsuario: Int = -1

    private var contadorSeries = 0
    private var contadorEjercicios = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_entrenamiento)

        idUsuario = intent.getIntExtra("idUsuario", -1)
        observarEstadoGuardado()

        vincularVistas()
        configurarChips()
        configurarDuracion()
        configurarPR()
        configurarGuardar()
        configurarCarrera()
        configurarFuerza()
        findViewById<View>(R.id.btnVolver).setOnClickListener { finish() }

        seleccionarTipo(TipoEntrenamiento.LIBRE)
    }

    private fun vincularVistas() {
        chips = mapOf(
            TipoEntrenamiento.CROSSFIT to findViewById(R.id.chipCrossfit),
            TipoEntrenamiento.CARRERA to findViewById(R.id.chipCarrera),
            TipoEntrenamiento.LIBRE to findViewById(R.id.chipLibre),
            TipoEntrenamiento.MIXTO to findViewById(R.id.chipMixto),
            TipoEntrenamiento.FUERZA to findViewById(R.id.chipFuerza)

        )

        groupDescripcionDuracion = findViewById(R.id.groupDescripcionDuracion)
        groupCarrera = findViewById(R.id.groupCarrera)
        groupFuerza = findViewById(R.id.groupFuerza)
        groupPR = findViewById(R.id.groupPR)
        groupCamposPR = findViewById(R.id.groupCamposPR)
        switchPR = findViewById(R.id.switchPR)

        etDescripcion = findViewById(R.id.etDescripcion)
        groupDuracionManual = findViewById(R.id.groupDuracionManual)
        etMinutosManual = findViewById(R.id.etMinutosManual)
        tvUsarCronometro = findViewById(R.id.tvUsarCronometro)

        groupCronometro = findViewById(R.id.groupCronometro)
        tvEstadoCronometro = findViewById(R.id.tvEstadoCronometro)
        tvTiempoCronometro = findViewById(R.id.tvTiempoCronometro)
        tvDuracionGuardada = findViewById(R.id.tvDuracionGuardada)
        tvEntrenamientoGuardado = findViewById(R.id.tvEntrenamientoGuardado)
        rowBotonesCronometro = findViewById(R.id.rowBotonesCronometro)
        btnIniciarCronometro = findViewById(R.id.btnIniciarCronometro)
        rowPausarFinalizar = findViewById(R.id.rowPausarFinalizar)
        btnPausar = findViewById(R.id.btnPausar)

        btnFinalizar = findViewById(R.id.btnFinalizar)
        contenedorSeries = findViewById(R.id.contenedorSeries)
        tvContadorSeries = findViewById(R.id.tvContadorSeries)
        btnAnadirSerie = findViewById(R.id.btnAnadirSerie)
        contenedorEjercicios = findViewById(R.id.contenedorEjercicios)
        btnAnadirEjercicio = findViewById(R.id.btnAnadirEjercicio)

        btnGuardarEntrenamiento = findViewById(R.id.btnGuardarEntrenamiento)
    }

    // ---------- CHIPS ----------

    private fun configurarChips() {
        chips.forEach { (tipo, boton) ->
            boton.setOnClickListener { seleccionarTipo(tipo) }
        }
    }

    private fun seleccionarTipo(tipo: TipoEntrenamiento) {
        tipoSeleccionado = tipo

        chips.forEach { (t, boton) ->
            val seleccionado = t == tipo
            boton.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(if (seleccionado) "#E31E24" else "#2A2A2A")
            )
        }

        // Mostrar/ocultar bloques según tipo
        groupDescripcionDuracion.visibility =
            if (tipo == TipoEntrenamiento.CROSSFIT || tipo == TipoEntrenamiento.LIBRE || tipo == TipoEntrenamiento.MIXTO)
                View.VISIBLE else View.GONE

        groupCarrera.visibility = if (tipo == TipoEntrenamiento.CARRERA) View.VISIBLE else View.GONE
        groupFuerza.visibility = if (tipo == TipoEntrenamiento.FUERZA) View.VISIBLE else View.GONE
        groupPR.visibility = if (tipo == TipoEntrenamiento.CROSSFIT) View.VISIBLE else View.GONE

        val etiquetaDescripcion = when (tipo) {
            TipoEntrenamiento.CROSSFIT -> "AMRAP 20 min · 10 burpees, 15 KB swings, 20 air squats."
            TipoEntrenamiento.MIXTO -> "Describe el WOD, los bloques…"
            else -> "Rodaje suave + movilidad."
        }
        etDescripcion.hint = etiquetaDescripcion
    }

    // ---------- DURACION: MANUAL / CRONOMETRO ----------

    private fun configurarDuracion() {
        tvUsarCronometro.setOnClickListener {
            groupDuracionManual.visibility = View.GONE
            groupCronometro.visibility = View.VISIBLE
            mostrarEstadoInicialCronometro()
        }

        btnIniciarCronometro.setOnClickListener { iniciarCronometro() }
        btnPausar.setOnClickListener { pausarCronometro() }
        btnFinalizar.setOnClickListener { finalizarCronometro() }

        runnableCronometro = object : Runnable {
            override fun run() {
                if (cronometroActivo) {
                    segundosTranscurridos++
                    actualizarTextoTiempo()
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    private fun mostrarEstadoInicialCronometro() {
        tvEstadoCronometro.text = "LISTO PARA EMPEZAR"
        tvEstadoCronometro.setTextColor(Color.parseColor("#8A8A8A"))
        tvTiempoCronometro.text = "00:00"
        rowBotonesCronometro.visibility = View.VISIBLE
        rowPausarFinalizar.visibility = View.GONE
        tvDuracionGuardada.visibility = View.GONE
        tvEntrenamientoGuardado.visibility = View.GONE
    }

    private fun iniciarCronometro() {
        cronometroActivo = true
        cronometroFinalizado = false
        tvEstadoCronometro.text = "ENTRENANDO · EN MARCHA"
        tvEstadoCronometro.setTextColor(Color.parseColor("#E31E24"))
        rowBotonesCronometro.visibility = View.GONE
        rowPausarFinalizar.visibility = View.VISIBLE
        btnPausar.text = "Pausar"
        handler.postDelayed(runnableCronometro, 1000)
    }

    private fun pausarCronometro() {
        if (cronometroActivo) {
            cronometroActivo = false
            btnPausar.text = "Reanudar"
            tvEstadoCronometro.text = "EN PAUSA"
            tvEstadoCronometro.setTextColor(Color.parseColor("#8A8A8A"))
        } else {
            cronometroActivo = true
            btnPausar.text = "Pausar"
            tvEstadoCronometro.text = "ENTRENANDO · EN MARCHA"
            tvEstadoCronometro.setTextColor(Color.parseColor("#E31E24"))
            handler.postDelayed(runnableCronometro, 1000)
        }
    }

    private fun finalizarCronometro() {
        cronometroActivo = false
        cronometroFinalizado = true
        handler.removeCallbacks(runnableCronometro)

        tvEstadoCronometro.text = "ENTRENAMIENTO FINALIZADO"
        tvEstadoCronometro.setTextColor(Color.parseColor("#2ECC71"))
        rowPausarFinalizar.visibility = View.GONE

        val minutos = segundosTranscurridos / 60
        tvDuracionGuardada.text = "✓  Duración guardada · $minutos min"
        tvDuracionGuardada.visibility = View.VISIBLE
        tvEntrenamientoGuardado.visibility = View.VISIBLE
    }

    private fun actualizarTextoTiempo() {
        val minutos = segundosTranscurridos / 60
        val segundos = segundosTranscurridos % 60
        tvTiempoCronometro.text = String.format("%02d:%02d", minutos, segundos)
    }

    // ---------- PR ----------

    private fun configurarPR() {
        switchPR.setOnCheckedChangeListener { _, marcado ->
            groupCamposPR.visibility = if (marcado) View.VISIBLE else View.GONE
        }
    }

    // ---------- GUARDAR ----------

    private fun configurarGuardar() {
        btnGuardarEntrenamiento.setOnClickListener {
            val duracionMinutos: Int

            when (tipoSeleccionado) {
                TipoEntrenamiento.CARRERA -> duracionMinutos = recogerDatosCarrera().second
                TipoEntrenamiento.FUERZA -> duracionMinutos = recogerDatosFuerza().second
                else -> duracionMinutos = obtenerDuracionEnMinutos()
            }

            val nombreTipo = when (tipoSeleccionado) {
                TipoEntrenamiento.CROSSFIT -> "CrossFit"
                TipoEntrenamiento.CARRERA -> "Carrera"
                TipoEntrenamiento.LIBRE -> "Libre"
                TipoEntrenamiento.MIXTO -> "Mixto"
                TipoEntrenamiento.FUERZA -> "Fuerza"
            }

            viewModel.guardarEntrenamiento(idUsuario, nombreTipo, duracionMinutos)
        }
    }

    // ---------- CARRERA ----------

    private fun configurarCarrera() {
        btnAnadirSerie.setOnClickListener { añadirSerie() }
        añadirSerie() // arranca con una serie ya puesta, como en el diseño
    }

    private fun añadirSerie() {
        contadorSeries++
        val inflater = layoutInflater
        val vista = inflater.inflate(R.layout.item_serie_carrera, contenedorSeries, false)

        val tvNumero = vista.findViewById<TextView>(R.id.tvNumeroSerie)
        val btnEliminar = vista.findViewById<Button>(R.id.btnEliminarSerie)

        tvNumero.text = "Serie $contadorSeries"
        btnEliminar.setOnClickListener {
            contenedorSeries.removeView(vista)
            renumerarSeries()
        }

        contenedorSeries.addView(vista)
        actualizarContadorSeries()
    }

    private fun renumerarSeries() {
        contadorSeries = contenedorSeries.childCount
        for (i in 0 until contenedorSeries.childCount) {
            val vista = contenedorSeries.getChildAt(i)
            vista.findViewById<TextView>(R.id.tvNumeroSerie).text = "Serie ${i + 1}"
        }
        actualizarContadorSeries()
    }

    private fun actualizarContadorSeries() {
        val total = contenedorSeries.childCount
        tvContadorSeries.text = if (total == 1) "1 serie" else "$total series"
    }

// ---------- FUERZA ----------

    private fun configurarFuerza() {
        btnAnadirEjercicio.setOnClickListener { añadirEjercicio() }
        añadirEjercicio() // arranca con un ejercicio ya puesto
    }

    private fun añadirEjercicio() {
        val inflater = layoutInflater
        val vista = inflater.inflate(R.layout.item_ejercicio_fuerza, contenedorEjercicios, false)

        val btnEliminar = vista.findViewById<Button>(R.id.btnEliminarEjercicio)
        btnEliminar.setOnClickListener { contenedorEjercicios.removeView(vista) }

        configurarStepper(
            vista.findViewById(R.id.tvSeriesValor),
            vista.findViewById(R.id.btnSeriesMenos),
            vista.findViewById(R.id.btnSeriesMas),
            paso = 1, minimo = 1
        )
        configurarStepper(
            vista.findViewById(R.id.tvRepsValor),
            vista.findViewById(R.id.btnRepsMenos),
            vista.findViewById(R.id.btnRepsMas),
            paso = 1, minimo = 1
        )
        configurarStepper(
            vista.findViewById(R.id.tvPesoValor),
            vista.findViewById(R.id.btnPesoMenos),
            vista.findViewById(R.id.btnPesoMas),
            paso = 5, minimo = 0
        )

        contenedorEjercicios.addView(vista)
    }

    private fun configurarStepper(tvValor: TextView, btnMenos: Button, btnMas: Button, paso: Int, minimo: Int) {
        btnMenos.setOnClickListener {
            val actual = tvValor.text.toString().toIntOrNull() ?: minimo
            val nuevo = (actual - paso).coerceAtLeast(minimo)
            tvValor.text = nuevo.toString()
        }
        btnMas.setOnClickListener {
            val actual = tvValor.text.toString().toIntOrNull() ?: minimo
            tvValor.text = (actual + paso).toString()
        }
    }

// ---------- RECOGER DATOS PARA GUARDAR ----------

    private fun recogerDatosCarrera(): Pair<String, Int> {
        var minutosTotales = 0
        val numeroSeries = contenedorSeries.childCount
        for (i in 0 until numeroSeries) {
            val vista = contenedorSeries.getChildAt(i)
            val tiempo = vista.findViewById<EditText>(R.id.etTiempoSerie).text.toString()
            val partes = tiempo.split(":")
            if (partes.size == 2) {
                val min = partes[0].toIntOrNull() ?: 0
                val seg = partes[1].toIntOrNull() ?: 0
                minutosTotales += min + (seg / 60)
            }
        }
        val descripcion = if (numeroSeries == 1) "Carrera · 1 serie" else "Carrera · $numeroSeries series"
        return Pair(descripcion, minutosTotales)
    }

    private fun recogerDatosFuerza(): Pair<String, Int> {
        val numeroEjercicios = contenedorEjercicios.childCount
        val nombres = mutableListOf<String>()
        for (i in 0 until numeroEjercicios) {
            val vista = contenedorEjercicios.getChildAt(i)
            val nombre = vista.findViewById<EditText>(R.id.etNombreEjercicio).text.toString().trim()
            if (nombre.isNotEmpty()) nombres.add(nombre)
        }
        val descripcion = if (nombres.isNotEmpty()) "Fuerza · ${nombres.joinToString(", ")}" else "Fuerza · $numeroEjercicios ejercicios"
        // Sin cronómetro en este tipo: estimamos con un valor fijo por ejercicio (ajustable)
        val minutosEstimados = numeroEjercicios * 8
        return Pair(descripcion, minutosEstimados)
    }

    private fun observarEstadoGuardado() {
        viewModel.estadoGuardado.observe(this) { estado ->
            when (estado) {
                is EstadoGuardado.Cargando -> {
                    btnGuardarEntrenamiento.isEnabled = false
                }
                is EstadoGuardado.Exito -> {
                    btnGuardarEntrenamiento.isEnabled = true
                    setResult(RESULT_OK)
                    finish()
                }
                is EstadoGuardado.Error -> {
                    btnGuardarEntrenamiento.isEnabled = true
                    etMinutosManual.error = estado.mensaje
                }
            }
        }
    }


    private fun obtenerDuracionEnMinutos(): Int {
        return if (groupCronometro.visibility == View.VISIBLE) {
            segundosTranscurridos / 60
        } else {
            etMinutosManual.text.toString().toIntOrNull() ?: 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnableCronometro)
    }
}