package com.marcos.fittrack.ui.perfil

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.marcos.fittrack.R
import com.marcos.fittrack.data.model.User
import com.marcos.fittrack.data.util.calcularEdad
import com.marcos.fittrack.ui.home.HomeActivity
import java.util.Calendar
import java.util.Locale

class DatosPersonalesActivity : AppCompatActivity() {

    private val viewModel: DatosPersonalesViewModel by viewModels()
    private var idUsuario: Int = -1
    private var esOnboarding = false

    /** Se guarda en formato ISO "yyyy-MM-dd" (lo que espera la API); el campo solo muestra dd/mm/aaaa. */
    private var fechaNacimientoIso: String? = null
    private var sexoSeleccionado: String? = null
    private var pasosObjetivo = 10000

    private lateinit var btnVolver: ImageButton
    private lateinit var tvLogo: TextView
    private lateinit var tvAvatarIniciales: TextView
    private lateinit var tvAvatarNombre: TextView
    private lateinit var tvAvatarSub: TextView
    private lateinit var etNombre: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var tvEdadChip: TextView
    private lateinit var btnSexoHombre: Button
    private lateinit var btnSexoMujer: Button
    private lateinit var btnSexoOtro: Button
    private lateinit var etAltura: EditText
    private lateinit var etPeso: EditText
    private lateinit var btnPasosMenos: TextView
    private lateinit var tvPasosValor: TextView
    private lateinit var btnPasosMas: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_personales)

        idUsuario = intent.getIntExtra("idUsuario", -1)

        btnVolver = findViewById(R.id.btnVolver)
        tvLogo = findViewById(R.id.tvLogo)
        tvAvatarIniciales = findViewById(R.id.tvAvatarIniciales)
        tvAvatarNombre = findViewById(R.id.tvAvatarNombre)
        tvAvatarSub = findViewById(R.id.tvAvatarSub)
        etNombre = findViewById(R.id.etNombre)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        tvEdadChip = findViewById(R.id.tvEdadChip)
        btnSexoHombre = findViewById(R.id.btnSexoHombre)
        btnSexoMujer = findViewById(R.id.btnSexoMujer)
        btnSexoOtro = findViewById(R.id.btnSexoOtro)
        etAltura = findViewById(R.id.etAltura)
        etPeso = findViewById(R.id.etPeso)
        btnPasosMenos = findViewById(R.id.btnPasosMenos)
        tvPasosValor = findViewById(R.id.tvPasosValor)
        btnPasosMas = findViewById(R.id.btnPasosMas)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)

        montarLogo()
        actualizarPasos()
        actualizarSegmentadoSexo()
        montarAvatar()

        btnVolver.setOnClickListener { finish() }
        btnCancelar.setOnClickListener { finish() }

        etFechaNacimiento.setOnClickListener { mostrarSelectorFecha() }

        btnSexoHombre.setOnClickListener { seleccionarSexo("male") }
        btnSexoMujer.setOnClickListener { seleccionarSexo("female") }
        btnSexoOtro.setOnClickListener { seleccionarSexo("other") }

        btnPasosMenos.setOnClickListener {
            pasosObjetivo = (pasosObjetivo - 500).coerceAtLeast(1000)
            actualizarPasos()
        }
        btnPasosMas.setOnClickListener {
            pasosObjetivo = (pasosObjetivo + 500).coerceAtMost(50000)
            actualizarPasos()
        }

        btnGuardar.setOnClickListener { guardar() }

        observarEstado()

        if (idUsuario != -1) {
            viewModel.cargar(idUsuario)
        } else {
            Toast.makeText(this, "No se pudo identificar al usuario", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun observarEstado() {
        viewModel.estado.observe(this) { estado ->
            when (estado) {
                is EstadoDatosPersonales.Cargando -> { /* reposo */ }
                is EstadoDatosPersonales.Cargado -> rellenarFormulario(estado.usuario)
                is EstadoDatosPersonales.Guardando -> btnGuardar.isEnabled = false
                is EstadoDatosPersonales.Guardado -> {
                    btnGuardar.isEnabled = true
                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    if (estado.esOnboarding) {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("idUsuario", idUsuario)
                        intent.putExtra("nombreUsuario", etNombre.text.toString().trim())
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    setResult(RESULT_OK)
                    finish()
                }
                is EstadoDatosPersonales.Error -> {
                    btnGuardar.isEnabled = true
                    mostrarError(estado.mensaje, estado.campo)
                }
            }
        }
    }

    private fun rellenarFormulario(usuario: User) {
        esOnboarding = usuario.dateOfBirth.isNullOrBlank()
        btnGuardar.text = if (esOnboarding) "Guardar y continuar" else "Guardar cambios"

        etNombre.setText(usuario.name)

        fechaNacimientoIso = usuario.dateOfBirth
        if (!usuario.dateOfBirth.isNullOrBlank()) {
            etFechaNacimiento.setText(formatearFechaVisible(usuario.dateOfBirth))
            mostrarEdad(usuario.age)
        }

        sexoSeleccionado = usuario.sex
        actualizarSegmentadoSexo()

        usuario.heightCm?.let { etAltura.setText(it.toString()) }
        usuario.weightKg?.let { etPeso.setText(formatearPeso(it)) }

        pasosObjetivo = usuario.dailyStepGoal
        actualizarPasos()

        montarAvatar(usuario.name, usuario.age)
    }

    /** Muestra el error en el campo al que pertenece (con foco, para que la
     *  burbuja sea visible); si no es de un campo concreto, usa un Toast. */
    private fun mostrarError(mensaje: String, campo: CampoDatosPersonales?) {
        val vistaCampo = when (campo) {
            CampoDatosPersonales.NOMBRE -> etNombre
            CampoDatosPersonales.FECHA_NACIMIENTO -> etFechaNacimiento
            null -> null
        }
        if (vistaCampo != null) {
            vistaCampo.requestFocus()
            vistaCampo.error = mensaje
        } else {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    private fun guardar() {
        val nombre = etNombre.text.toString().trim()
        val altura = etAltura.text.toString().trim().toIntOrNull()
        val peso = etPeso.text.toString().trim().replace(",", ".").toDoubleOrNull()

        viewModel.guardar(
            userId = idUsuario,
            nombre = nombre,
            fechaNacimiento = fechaNacimientoIso,
            sexo = sexoSeleccionado,
            alturaCm = altura,
            pesoKg = peso,
            objetivoPasos = pasosObjetivo,
            esOnboarding = esOnboarding
        )
    }

    private fun mostrarSelectorFecha() {
        val calInicial = Calendar.getInstance()
        val iso = fechaNacimientoIso
        if (!iso.isNullOrBlank()) {
            val partes = iso.split("-")
            if (partes.size == 3) {
                calInicial.set(partes[0].toInt(), partes[1].toInt() - 1, partes[2].toInt())
            }
        } else {
            calInicial.add(Calendar.YEAR, -EDAD_MINIMA)
        }

        // Tope máximo: no se puede elegir una fecha que dé menos de 18 años.
        val calMaxima = Calendar.getInstance().apply { add(Calendar.YEAR, -EDAD_MINIMA) }

        val dialogo = DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                val iso2 = "%04d-%02d-%02d".format(anio, mes + 1, dia)
                fechaNacimientoIso = iso2
                etFechaNacimiento.setText(formatearFechaVisible(iso2))
                mostrarEdad(calcularEdad(iso2))
            },
            calInicial.get(Calendar.YEAR),
            calInicial.get(Calendar.MONTH),
            calInicial.get(Calendar.DAY_OF_MONTH)
        )
        dialogo.datePicker.maxDate = calMaxima.timeInMillis
        dialogo.show()
    }

    private fun mostrarEdad(edad: Int?) {
        if (edad == null) {
            tvEdadChip.visibility = android.view.View.GONE
        } else {
            tvEdadChip.text = "$edad años"
            tvEdadChip.visibility = android.view.View.VISIBLE
        }
    }

    private fun seleccionarSexo(valor: String) {
        sexoSeleccionado = valor
        actualizarSegmentadoSexo()
    }

    private fun actualizarSegmentadoSexo() {
        val botones = mapOf(
            "male" to btnSexoHombre,
            "female" to btnSexoMujer,
            "other" to btnSexoOtro
        )
        for ((valor, boton) in botones) {
            val activo = valor == sexoSeleccionado
            boton.backgroundTintList = android.content.res.ColorStateList.valueOf(
                if (activo) getColor(R.color.fittrack_red) else Color.TRANSPARENT
            )
            boton.setTextColor(if (activo) Color.WHITE else getColor(R.color.fittrack_text_gray))
        }
    }

    private fun actualizarPasos() {
        val formato = java.text.NumberFormat.getIntegerInstance(Locale("es", "ES"))
        tvPasosValor.text = formato.format(pasosObjetivo)
    }

    private fun montarAvatar(nombre: String = "", edad: Int? = null) {
        tvAvatarIniciales.text = iniciales(nombre)
        tvAvatarNombre.text = nombre.ifBlank { "Nombre completo" }
        tvAvatarSub.text = if (edad != null) "$edad años" else "Edad sin definir"
    }

    private fun iniciales(nombre: String): String {
        val partes = nombre.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return when {
            partes.isEmpty() -> "?"
            partes.size == 1 -> partes[0].take(1).uppercase()
            else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
        }
    }

    private fun formatearFechaVisible(iso: String): String {
        val partes = iso.split("-")
        return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else iso
    }

    private fun formatearPeso(valor: Double): String {
        return if (valor == Math.floor(valor)) {
            valor.toLong().toString()
        } else {
            String.format(Locale("es", "ES"), "%.1f", valor)
        }
    }

    private fun montarLogo() {
        val texto = "FITTRACK+"
        val spannable = SpannableString(texto)
        spannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#E31E24")), 3, texto.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvLogo.text = spannable
    }
}
