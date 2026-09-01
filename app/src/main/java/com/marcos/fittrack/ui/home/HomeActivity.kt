package com.marcos.fittrack.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.marcos.fittrack.R
import com.marcos.fittrack.data.model.Workout
import com.marcos.fittrack.ui.entrenamiento.NuevoEntrenamientoActivity
import java.text.SimpleDateFormat
import java.util.Locale
import com.marcos.fittrack.ui.ejercicios.EjerciciosActivity

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private var idUsuario: Int = -1

    private lateinit var tvLogo: TextView
    private lateinit var tvSaludo: TextView
    private lateinit var tvResumenVacio: TextView
    private lateinit var groupResumenConDatos: LinearLayout
    private lateinit var tvNumeroEntrenos: TextView
    private lateinit var tvTiempoTotal: TextView
    private lateinit var tvPorcentajePasos: TextView
    private lateinit var tvPasosHoy: TextView
    private lateinit var tvHistorialVacio: TextView
    private lateinit var contenedorHistorial: LinearLayout
    private lateinit var tvConsejoDia: TextView
    private lateinit var tvTotalEjercicios: TextView

    private val metaPasosPorDefecto = 10000

    private val lanzadorNuevoEntrenamiento =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                // Se guardó un entrenamiento nuevo: recargamos los datos reales desde la API
                viewModel.cargarDatos(idUsuario)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        idUsuario = intent.getIntExtra("idUsuario", -1)
        val nombre = intent.getStringExtra("nombreUsuario") ?: "Usuario"

        tvLogo = findViewById(R.id.tvLogo)
        tvSaludo = findViewById(R.id.tvSaludo)
        tvResumenVacio = findViewById(R.id.tvResumenVacio)
        groupResumenConDatos = findViewById(R.id.groupResumenConDatos)
        tvNumeroEntrenos = findViewById(R.id.tvNumeroEntrenos)
        tvTiempoTotal = findViewById(R.id.tvTiempoTotal)
        tvPorcentajePasos = findViewById(R.id.tvPorcentajePasos)
        tvPasosHoy = findViewById(R.id.tvPasosHoy)
        tvHistorialVacio = findViewById(R.id.tvHistorialVacio)
        contenedorHistorial = findViewById(R.id.contenedorHistorial)
        tvConsejoDia = findViewById(R.id.tvConsejoDia)
        tvTotalEjercicios = findViewById(R.id.tvTotalEjercicios)

        montarLogo()
        tvSaludo.text = "Buenas, $nombre"
        tvTotalEjercicios.text = "1.324 ejercicios"
        montarConsejo()

        observarEstado()

        if (idUsuario != -1) {
            viewModel.cargarDatos(idUsuario)
        }

        findViewById<View>(R.id.btnNuevoEntrenamiento).setOnClickListener {
            val intent = Intent(this, NuevoEntrenamientoActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            lanzadorNuevoEntrenamiento.launch(intent)
        }

        findViewById<View>(R.id.btnAjustes).setOnClickListener {
            // TODO: abrir ajustes
        }

        findViewById<View>(R.id.cardExplorarEjercicios).setOnClickListener {
            startActivity(Intent(this, EjerciciosActivity::class.java))
        }
    }

    private fun observarEstado() {
        viewModel.estadoHome.observe(this) { estado ->
            when (estado) {
                is EstadoHome.Cargando -> {
                    // Podrías mostrar un spinner aquí si quieres
                }
                is EstadoHome.Exito -> {
                    montarResumenSemana(estado.datos.entrenamientosSemana)
                    montarPasos(estado.datos.actividadHoy?.steps ?: 0)
                    montarHistorial(estado.datos.entrenamientosSemana)
                }
                is EstadoHome.Error -> {
                    tvResumenVacio.visibility = View.VISIBLE
                    tvResumenVacio.text = estado.mensaje
                    groupResumenConDatos.visibility = View.GONE
                }
            }
        }
    }

    private fun montarLogo() {
        val texto = "FITTRACK"
        val spannable = SpannableString(texto)
        spannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#E31E24")), 3, texto.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvLogo.text = spannable
    }

    private fun montarResumenSemana(entrenamientos: List<Workout>) {
        if (entrenamientos.isEmpty()) {
            tvResumenVacio.visibility = View.VISIBLE
            groupResumenConDatos.visibility = View.GONE
            tvResumenVacio.text = "Aún no has registrado nada esta semana. Toca el botón + para empezar."
        } else {
            tvResumenVacio.visibility = View.GONE
            groupResumenConDatos.visibility = View.VISIBLE
            tvNumeroEntrenos.text = entrenamientos.size.toString()

            val minutosTotales = entrenamientos.sumOf { it.durationSeconds / 60 }
            tvTiempoTotal.text = if (minutosTotales >= 60) {
                "${minutosTotales / 60}h${String.format("%02d", minutosTotales % 60)}"
            } else {
                "$minutosTotales min"
            }
        }
    }

    private fun montarPasos(pasosHoy: Int) {
        val porcentaje = ((pasosHoy.toFloat() / metaPasosPorDefecto) * 100).toInt().coerceAtMost(100)
        tvPorcentajePasos.text = "$porcentaje%"
        tvPasosHoy.text = "${"%,d".format(pasosHoy).replace(",", ".")} pasos hoy"
    }

    private fun montarHistorial(entrenamientos: List<Workout>) {
        contenedorHistorial.removeAllViews()

        if (entrenamientos.isEmpty()) {
            tvHistorialVacio.visibility = View.VISIBLE
            return
        }
        tvHistorialVacio.visibility = View.GONE

        val ordenados = entrenamientos.sortedByDescending { it.startedAt }
        val inflater = LayoutInflater.from(this)

        for (entrenamiento in ordenados) {
            val vista = inflater.inflate(R.layout.item_entrenamiento, contenedorHistorial, false)

            val viewDot = vista.findViewById<View>(R.id.viewDot)
            val tvNombre = vista.findViewById<TextView>(R.id.tvNombreEntrenamiento)
            val tvFecha = vista.findViewById<TextView>(R.id.tvFechaEntrenamiento)
            val tvDuracion = vista.findViewById<TextView>(R.id.tvDuracionEntrenamiento)

            viewDot.backgroundTintList = android.content.res.ColorStateList.valueOf(
                Color.parseColor(colorParaTipo(entrenamiento.typeCode))
            )
            tvNombre.text = entrenamiento.typeName
            tvFecha.text = formatearFecha(entrenamiento.startedAt)
            tvDuracion.text = "${entrenamiento.durationSeconds / 60} min"

            contenedorHistorial.addView(vista)
        }
    }

    private fun formatearFecha(fechaTexto: String): String {
        return try {
            val entrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val salida = SimpleDateFormat("dd/MM · HH:mm", Locale.getDefault())
            val fecha = entrada.parse(fechaTexto.take(19))
            if (fecha != null) salida.format(fecha) else fechaTexto
        } catch (e: Exception) {
            fechaTexto
        }
    }

    private fun colorParaTipo(codigoTipo: String): String {
        return when (codigoTipo.lowercase()) {
            "crossfit" -> "#E31E24"
            "running" -> "#F5A623"
            "cycling" -> "#16A085"
            "swimming" -> "#2980B9"
            "mixed" -> "#9B59B6"
            "strength" -> "#3B82F6"
            else -> "#8A8A8A" // free u otros
        }
    }

    private fun montarConsejo() {
        tvConsejoDia.text = "Hoy tocaría descanso activo: un paseo de 20 min mantiene el ritmo sin sobrecargar."
    }
}