package com.upc.appgestiones.ui.operaciones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.core.utils.DateUtil
import kotlin.math.absoluteValue

class OperacionesAdapter(
    private var lista: List<Operacion>,
    private val onItemClick: (Operacion) -> Unit
) : RecyclerView.Adapter<OperacionesAdapter.OperacionViewHolder>() {

    inner class OperacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreCliente: TextView = itemView.findViewById(R.id.txtNombreCliente)
        val txtDireccion: TextView = itemView.findViewById(R.id.txtDireccion)
        val txtFecVencimiento: TextView = itemView.findViewById(R.id.txtFechaVencimiento)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        val txtTipo: TextView = itemView.findViewById(R.id.txtTipo)
        val txtDiasVencimiento: TextView = itemView.findViewById(R.id.txtDiasVencimiento)

        val cardOperacion: MaterialCardView = itemView.findViewById(R.id.cardOperacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_operacion, parent, false)
        return OperacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OperacionViewHolder, position: Int) {
        val operacion = lista[position]

        holder.txtNombreCliente.text = buildString {
            append(operacion.clienteNavigation.nombres.uppercase())
            append(" ")
            append(operacion.clienteNavigation.apellidos.uppercase())
        }
        holder.txtDireccion.text = buildString {
            append(operacion.direccionNavigation.calle.uppercase())
            append(" ")
            append(operacion.direccionNavigation.numero)
        }
        holder.txtEstado.text = buildString {
            append("Estado: ")
            append(operacion.estado.name.replace('_', ' '))
        }
        holder.txtTipo.text = buildString {
            append("Tipo Operación: ")
            append(operacion.tipo.name)
        }
        holder.txtFecVencimiento.text = buildString {
            append("Fec. Vencimiento: ")
            append(operacion.fechaVencimiento)
        }

        val diasRestantes = DateUtil.diferenciaDeFechaActual(operacion.fechaVencimiento)

        val context = holder.itemView.context
        val cardView = holder.itemView.findViewById<MaterialCardView>(R.id.cardOperacion)

        when {
            diasRestantes < 0 -> {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.card_red)
                )
                holder.txtDiasVencimiento.text = buildString {
                    append("Días de atraso: ")
                    append(diasRestantes.absoluteValue)
                }
            }
            diasRestantes in 0..3 -> {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.card_orange)
                )
                holder.txtDiasVencimiento.text = buildString {
                    append("Faltan ")
                    append(diasRestantes)
                    append(" día(s) para vencer")
                }
            }
            else -> {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.card_green)
                )
                holder.txtDiasVencimiento.text = "Operación al día"
            }
        }

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