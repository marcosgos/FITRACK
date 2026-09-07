package com.marcos.fittrack.ui.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.marcos.fittrack.R
import com.marcos.fittrack.data.auth.GoogleAuthHelper
import com.marcos.fittrack.ui.home.HomeActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnMostrarContrasena: ImageButton
    private lateinit var btnRegistrarse: Button
    private lateinit var btnGoogle: Button

    private var contrasenaVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnMostrarContrasena = findViewById(R.id.btnMostrarContrasena)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        btnGoogle = findViewById(R.id.btnGoogle)

        montarLogo()
        montarDisclaimer()
        configurarMostrarContrasena()
        observarEstado()

        btnRegistrarse.setOnClickListener {
            viewModel.registrar(
                nombre = etNombre.text.toString().trim(),
                correo = etCorreo.text.toString().trim(),
                contrasena = etContrasena.text.toString().trim()
            )
        }

        // NUEVO: mismo flujo de Credential Manager que en LoginActivity; la
        // API decide si la cuenta ya existe (login) o hay que crearla (alta).
        btnGoogle.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val idToken = GoogleAuthHelper.obtenerIdToken(this@RegisterActivity)
                    viewModel.registrarConGoogle(idToken)
                } catch (e: GoogleAuthHelper.CancelledException) {
                    // El usuario cerró el selector de cuentas: no hacemos nada.
                } catch (e: Exception) {
                    // Se captura cualquier fallo (no solo GetCredentialException) para
                    // no dejar el botón "muerto" sin feedback si algo falla.
                    val tipo = if (e is GetCredentialException) e.type else e::class.simpleName
                    android.util.Log.e("GoogleRegister", "Error real: $tipo", e)
                    Toast.makeText(
                        this@RegisterActivity,
                        "No se pudo continuar con Google",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
                    mostrarError(estado.mensaje, estado.campo)
                }
            }
        }
    }

    /** Muestra el error en el campo al que pertenece (con foco, para que la
     *  burbuja sea visible); si no es de un campo concreto, usa un Toast. */
    private fun mostrarError(mensaje: String, campo: CampoRegistro?) {
        val vistaCampo = when (campo) {
            CampoRegistro.NOMBRE -> etNombre
            CampoRegistro.CORREO -> etCorreo
            CampoRegistro.CONTRASENA -> etContrasena
            null -> null
        }
        if (vistaCampo != null) {
            vistaCampo.requestFocus()
            vistaCampo.error = mensaje
        } else {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    private fun configurarMostrarContrasena() {
        btnMostrarContrasena.setOnClickListener {
            contrasenaVisible = !contrasenaVisible
            etContrasena.inputType = if (contrasenaVisible) {
                btnMostrarContrasena.setImageResource(R.drawable.ic_eye_off)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                btnMostrarContrasena.setImageResource(R.drawable.ic_eye)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            etContrasena.setSelection(etContrasena.text.length)
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
