package com.upc.appgestiones.ui.operaciones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Operacion

class OperacionesAdapter(
    private var lista: List<Operacion>,
    private val onItemClick: (Operacion) -> Unit
) : RecyclerView.Adapter<OperacionesAdapter.OperacionViewHolder>() {

    inner class OperacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreCliente: TextView = itemView.findViewById(R.id.txtNombreCliente)
        val txtDireccion: TextView = itemView.findViewById(R.id.txtDireccion)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        val txtTipo: TextView = itemView.findViewById(R.id.txtTipo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_operacion, parent, false)
        return OperacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OperacionViewHolder, position: Int) {
        val operacion = lista[position]

        holder.txtNombreCliente.text =
            "${operacion.clienteNavigation.nombres} ${operacion.clienteNavigation.apellidos}"
        holder.txtDireccion.text =
            "${operacion.direccionNavigation.calle} ${operacion.direccionNavigation.numero}"
        holder.txtEstado.text = operacion.estado.name
        holder.txtTipo.text = operacion.tipo.name

        holder.itemView.setOnClickListener { onItemClick(operacion) }

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime = 0L
            override fun onClick(v: View?) {
                val now = System.currentTimeMillis()
                if (now - lastClickTime < 300) {

                    onItemClick(operacion)
                }
                lastClickTime = now
            }
        })


    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<Operacion>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }


}