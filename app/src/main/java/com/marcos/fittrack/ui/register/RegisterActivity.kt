package com.marcos.fittrack.ui.register

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.marcos.fittrack.R
import com.marcos.fittrack.ui.home.HomeActivity
import java.util.Calendar
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var etNombre: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnRegistrarse: Button
    private lateinit var btnGoogle: Button
    private lateinit var btnApple: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNombre = findViewById(R.id.etNombre)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnApple = findViewById(R.id.btnApple)

        montarLogo()
        montarDisclaimer()
        configurarSelectorFecha()
        observarEstado()

        btnRegistrarse.setOnClickListener {
            viewModel.registrar(
                nombre = etNombre.text.toString().trim(),
                fechaNacimiento = etFechaNacimiento.text.toString().trim(),
                correo = etCorreo.text.toString().trim(),
                contrasena = etContrasena.text.toString().trim()
            )
        }

        btnGoogle.setOnClickListener {
            // TODO: registro con Google
        }

        btnApple.setOnClickListener {
            // TODO: registro con Apple
        }
    }

    private fun observarEstado() {
        viewModel.estadoRegistro.observe(this) { estado ->
            when (estado) {
                is EstadoRegistro.Inicial -> { /* reposo */ }
                is EstadoRegistro.Cargando -> {
                    btnRegistrarse.isEnabled = false
                }
                is EstadoRegistro.Exito -> {
                    btnRegistrarse.isEnabled = true
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("nombreUsuario", estado.nombre)
                    intent.putExtra("idUsuario", estado.idUsuario)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is EstadoRegistro.Error -> {
                    btnRegistrarse.isEnabled = true
                    etContrasena.error = estado.mensaje
                }
            }
        }
    }

    private fun configurarSelectorFecha() {
        etFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                { _, anioSel, mesSel, diaSel ->
                    val fechaFormateada = String.format(
                        Locale.getDefault(), "%02d/%02d/%04d", diaSel, mesSel + 1, anioSel
                    )
                    etFechaNacimiento.setText(fechaFormateada)
                },
                anio, mes, dia
            ).show()
        }
    }

    private fun montarLogo() {
        val tvLogo = findViewById<TextView>(R.id.tvLogo)
        val texto = "FITTRACK"
        val spannable = SpannableString(texto)
        spannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#E31E24")), 3, texto.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvLogo.text = spannable
    }

    private fun montarDisclaimer() {
        val tvDisclaimer = findViewById<TextView>(R.id.tvDisclaimer)
        val texto = "Al hacer clic en continuar, aceptas nuestros Términos de Servicio y nuestra Política de Privacidad"
        val spannable = SpannableString(texto)

        val inicioTerminos = texto.indexOf("Términos de Servicio")
        val finTerminos = inicioTerminos + "Términos de Servicio".length
        val inicioPolitica = texto.indexOf("Política de Privacidad")
        val finPolitica = inicioPolitica + "Política de Privacidad".length

        spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), inicioTerminos, finTerminos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) { /* TODO */ }
        }, inicioTerminos, finTerminos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), inicioPolitica, finPolitica, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) { /* TODO */ }
        }, inicioPolitica, finPolitica, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvDisclaimer.text = spannable
        tvDisclaimer.movementMethod = LinkMovementMethod.getInstance()
    }
}