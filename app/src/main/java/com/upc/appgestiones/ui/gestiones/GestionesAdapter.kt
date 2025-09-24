package com.upc.appgestiones.ui.gestiones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Gestion

class GestionesAdapter(
    private val gestiones: List<Gestion>,
    private val onItemClick: (Gestion) -> Unit
) : RecyclerView.Adapter<GestionesAdapter.GestionViewHolder>() {

    inner class GestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        private val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        private val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        private val imgEvidencia: ImageView = itemView.findViewById(R.id.imgEvidencia)

        fun bind(gestion: Gestion) {
            txtCliente.text = "Operación: ${gestion.operacionNavigation.asunto}"
            txtFecha.text = "Fecha: ${gestion.fechaRegistro}"
            txtEstado.text = "Estado: ${gestion.operacionNavigation.estado}"

            // Mostrar evidencia si existe
            if (!gestion.urlFotoEvidencia.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(gestion.urlFotoEvidencia)
                    .into(imgEvidencia)
            } else {
                imgEvidencia.setImageResource(R.drawable.img_cer) // Ícono por defecto
            }

            itemView.setOnClickListener {
                onItemClick(gestion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gestion, parent, false)
        return GestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: GestionViewHolder, position: Int) {
        holder.bind(gestiones[position])
    }

    override fun getItemCount(): Int = gestiones.size
}

