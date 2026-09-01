package com.marcos.fittrack.ui.ejercicios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.marcos.fittrack.R
import com.marcos.fittrack.data.model.Ejercicio
import com.marcos.fittrack.data.util.Diccionario

class EjercicioAdapter(
    private var items: List<Ejercicio>,
    private val onClick: (Ejercicio) -> Unit
) : RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder>() {

    class EjercicioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMiniatura: ImageView = view.findViewById(R.id.ivMiniatura)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreEjercicio)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoriaEjercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EjercicioViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ejercicio_lista, parent, false)
        return EjercicioViewHolder(vista)
    }

    override fun onBindViewHolder(holder: EjercicioViewHolder, position: Int) {
        val ejercicio = items[position]

        holder.tvNombre.text = ejercicio.name
        holder.tvCategoria.text = listOfNotNull(
            ejercicio.body_part?.let { Diccionario.traducir(it) },
            ejercicio.equipment?.let { Diccionario.traducir(it) }
        ).joinToString(" · ")

        Glide.with(holder.itemView.context)
            .load(ejercicio.urlGif() ?: ejercicio.urlImagen())
            .placeholder(R.drawable.bg_circle_gray)
            .error(R.drawable.bg_circle_gray)
            .centerCrop()
            .into(holder.ivMiniatura)

        holder.itemView.setOnClickListener { onClick(ejercicio) }
    }

    override fun getItemCount(): Int = items.size

    fun actualizar(nuevaLista: List<Ejercicio>) {
        items = nuevaLista
        notifyDataSetChanged()
    }
}