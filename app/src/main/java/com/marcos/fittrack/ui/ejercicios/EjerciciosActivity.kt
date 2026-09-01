package com.marcos.fittrack.ui.ejercicios

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marcos.fittrack.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EjerciciosActivity : AppCompatActivity() {

    private val viewModel: EjerciciosViewModel by viewModels()

    private lateinit var etBuscar: EditText
    private lateinit var rvEjercicios: RecyclerView
    private lateinit var tvMensajeEstado: TextView
    private lateinit var adapter: EjercicioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejercicios)

        etBuscar = findViewById(R.id.etBuscar)
        rvEjercicios = findViewById(R.id.rvEjercicios)
        tvMensajeEstado = findViewById(R.id.tvMensajeEstado)

        findViewById<View>(R.id.btnVolver).setOnClickListener { finish() }

        adapter = EjercicioAdapter(emptyList()) { ejercicio ->
            val intent = Intent(this, DetalleEjercicioActivity::class.java)
            intent.putExtra("ejercicio", ejercicio)
            startActivity(intent)
        }
        rvEjercicios.layoutManager = LinearLayoutManager(this)
        rvEjercicios.adapter = adapter

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filtrar(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        observarEstado()
        viewModel.cargar()
    }

    private fun observarEstado() {
        viewModel.estado.observe(this) { estado ->
            when (estado) {
                is EstadoEjercicios.Cargando -> {
                    tvMensajeEstado.visibility = View.VISIBLE
                    tvMensajeEstado.text = "Cargando ejercicios…"
                    rvEjercicios.visibility = View.GONE
                }
                is EstadoEjercicios.Exito -> {
                    tvMensajeEstado.visibility = View.GONE
                    rvEjercicios.visibility = View.VISIBLE
                }
                is EstadoEjercicios.Error -> {
                    tvMensajeEstado.visibility = View.VISIBLE
                    tvMensajeEstado.text = estado.mensaje
                    rvEjercicios.visibility = View.GONE
                }
            }
        }

        viewModel.listaFiltrada.observe(this) { lista ->
            adapter.actualizar(lista)
        }
    }
}