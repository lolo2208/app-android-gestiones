package com.upc.appgestiones.ui.contacto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Contacto

class ContactosAdapter(
    private val contactos: List<Contacto>
) : RecyclerView.Adapter<ContactosAdapter.ContactoViewHolder>() {

    class ContactoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        val tvValor: TextView = itemView.findViewById(R.id.tvValor)
        val card: MaterialCardView = itemView as MaterialCardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacto, parent, false)
        return ContactoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        val contacto = contactos[position]
        holder.tvTipo.text = contacto.tipo
        holder.tvValor.text = contacto.valor
    }

    override fun getItemCount(): Int = contactos.size
}
