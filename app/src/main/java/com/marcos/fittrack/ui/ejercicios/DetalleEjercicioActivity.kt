package com.marcos.fittrack.ui.ejercicios

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.marcos.fittrack.R
import com.marcos.fittrack.data.model.Ejercicio
import com.marcos.fittrack.data.util.Diccionario

class DetalleEjercicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_ejercicio)

        val ejercicio = obtenerEjercicioDelIntent()
        if (ejercicio == null) {
            finish()
            return
        }

        findViewById<View>(R.id.btnVolver).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvNombre).text = ejercicio.name

        Glide.with(this)
            .load(ejercicio.urlGif() ?: ejercicio.urlImagen())
            .centerCrop()
            .into(findViewById<ImageView>(R.id.ivImagenGrande))

        montarBadges(ejercicio)
        montarMusculos(ejercicio)
        montarInstrucciones(ejercicio)
    }

    @Suppress("DEPRECATION")
    private fun obtenerEjercicioDelIntent(): Ejercicio? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("ejercicio", Ejercicio::class.java)
        } else {
            intent.getSerializableExtra("ejercicio") as? Ejercicio
        }
    }

    private fun montarBadges(ejercicio: Ejercicio) {
        val contenedor = findViewById<LinearLayout>(R.id.rowBadges)
        val textos = listOfNotNull(ejercicio.body_part, ejercicio.equipment, ejercicio.category)
            .map { Diccionario.traducir(it) }

        for (texto in textos) {
            val badge = TextView(this).apply {
                text = texto
                setTextColor(Color.parseColor("#E8A33D"))
                textSize = 11f
                setPadding(28, 14, 28, 14)
                background = getDrawable(R.drawable.bg_badge)
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 10
            badge.layoutParams = params
            contenedor.addView(badge)
        }
    }

    private fun montarMusculos(ejercicio: Ejercicio) {
        val principal = Diccionario.traducir(ejercicio.muscle_group).ifBlank { "No especificado" }
        val secundarios = ejercicio.secondary_muscles.map { Diccionario.traducir(it) }

        val texto = buildString {
            append(principal)
            if (secundarios.isNotEmpty()) {
                append("\n\nSecundarios: ")
                append(secundarios.joinToString(", "))
            }
        }
        findViewById<TextView>(R.id.tvMusculos).text = texto
    }

    private fun montarInstrucciones(ejercicio: Ejercicio) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorInstrucciones)
        contenedor.removeAllViews()

        val texto = ejercicio.descripcion()
        val tv = TextView(this).apply {
            text = texto.ifBlank { "Sin instrucciones disponibles para este ejercicio." }
            setTextColor(Color.parseColor("#EDEDED"))
            textSize = 13f
            setLineSpacing(6f, 1f)
        }
        contenedor.addView(tv)
    }
}