package com.upc.appgestiones.ui.gestiones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Catalogo
import com.upc.appgestiones.core.data.model.DetalleCatalogo
import com.upc.appgestiones.core.data.model.Gestion

class GestionesAdapter(
    private val gestiones: List<Gestion>,
    private val onItemClick: (Gestion) -> Unit
) : RecyclerView.Adapter<GestionesAdapter.GestionViewHolder>() {

    inner class GestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtRespuesta: TextView = itemView.findViewById(R.id.txtRespuesta)
        private val txtCliente: TextView = itemView.findViewById(R.id.txtNombreCliente)
        private val txtDireccion: TextView = itemView.findViewById(R.id.txtDireccion)
        private val txtFechaRegistro: TextView = itemView.findViewById(R.id.txtFechaRegistro)
        private val txtTipo: TextView = itemView.findViewById(R.id.txtTipo)

        private val txtIdOperacion: TextView = itemView.findViewById(R.id.txtIdOperacion)
        //private val imgEvidencia: ImageView = itemView.findViewById(R.id.imgEvidencia)

        fun bind(gestion: Gestion) {
            val catalogoRespuesta: Catalogo = Catalogo.fetchCatalogos().find { catalogo -> catalogo.codigoCatalogo == "RESPUESTAS_GESTION" } as Catalogo
            val detalleRespuesta: DetalleCatalogo = catalogoRespuesta.detallesCatalogo.find { detalleCatalogo -> detalleCatalogo.codigoDetalle == gestion.respuesta } as DetalleCatalogo

            txtRespuesta.text = "${ detalleRespuesta.descripcion }"
            txtFechaRegistro.text = "Fecha: ${gestion.fechaRegistro.replace('T', ' ')}"
            txtCliente.text = "Cliente: ${gestion.operacionNavigation!!.clienteNavigation.nombres} ${gestion.operacionNavigation!!.clienteNavigation.apellidos}"
            txtDireccion.text = "Dirección: ${gestion.operacionNavigation!!.direccionNavigation.calle} ${gestion.operacionNavigation!!.direccionNavigation.numero}"
            txtTipo.text = "${ gestion.operacionNavigation.tipo }"
            txtIdOperacion.text = "${gestion.idOperacion}"

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

