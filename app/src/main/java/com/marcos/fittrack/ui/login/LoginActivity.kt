package com.marcos.fittrack.ui.login

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
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.marcos.fittrack.R
import com.marcos.fittrack.data.auth.GoogleAuthHelper
import com.marcos.fittrack.ui.home.HomeActivity
import com.marcos.fittrack.ui.register.RegisterActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnGoogle: Button
    private lateinit var btnApple: Button
    private lateinit var tvIrACrearCuenta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnApple = findViewById(R.id.btnApple)
        tvIrACrearCuenta = findViewById(R.id.tvIrACrearCuenta)

        montarLogo()
        montarDisclaimer()
        observarEstado()

        btnIniciarSesion.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            viewModel.iniciarSesion(correo, contrasena)
        }

        // NUEVO: pide el ID token vía Credential Manager y se lo pasa al
        // ViewModel, que lo manda a /auth/google (mismo estado que el login normal).
        btnGoogle.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val idToken = GoogleAuthHelper.obtenerIdToken(this@LoginActivity)
                    viewModel.iniciarSesionConGoogle(idToken)
                } catch (e: GoogleAuthHelper.CancelledException) {
                    // El usuario cerró el selector de cuentas: no hacemos nada.
                } catch (e: GetCredentialException) {
                    etContrasena.error = "No se pudo iniciar sesión con Google"
                }
            }
        }

        btnApple.setOnClickListener {
            // TODO: login con Apple
        }

        tvIrACrearCuenta.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observarEstado() {
        viewModel.estadoLogin.observe(this) { estado ->
            when (estado) {
                is EstadoLogin.Inicial -> {
                    // no hacer nada, estado de reposo
                }
                is EstadoLogin.Cargando -> {
                    btnIniciarSesion.isEnabled = false
                }
                is EstadoLogin.Exito -> {
                    btnIniciarSesion.isEnabled = true
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("nombreUsuario", estado.usuario.name)
                    intent.putExtra("idUsuario", estado.usuario.userId)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is EstadoLogin.Error -> {
                    btnIniciarSesion.isEnabled = true
                    etContrasena.error = estado.mensaje
                }
            }
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