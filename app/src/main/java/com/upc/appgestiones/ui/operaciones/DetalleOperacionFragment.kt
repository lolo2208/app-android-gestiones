package com.upc.appgestiones.ui.operaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Operacion

class DetalleOperacionFragment : Fragment() {

    private var operacion: Operacion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operacion = arguments?.getSerializable("operacion") as? Operacion
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_operacion, container, false)

        val imgCliente = view.findViewById<ImageView>(R.id.imgCliente)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtEstado = view.findViewById<TextView>(R.id.txtEstado)
        val txtDireccion = view.findViewById<TextView>(R.id.txtDireccion)
        val txtTelefono = view.findViewById<TextView>(R.id.txtTelefono)
        val txtExtra = view.findViewById<TextView>(R.id.txtExtra)
        val txtGlosa = view.findViewById<TextView>(R.id.txtGlosa)

        operacion?.let {

            imgCliente.setImageResource(R.drawable.ic_user)

            txtNombre.text = "${it.clienteNavigation.nombres} ${it.clienteNavigation.apellidos}"
            txtEstado.text = it.estado.name
            txtDireccion.text =
                "${it.direccionNavigation.calle} ${it.direccionNavigation.numero}, ${it.direccionNavigation.ciudad}"
            txtTelefono.text = "DNI: ${it.clienteNavigation.documento}"
            txtExtra.text = "Fecha nacimiento: 01/01/1990 • Nacionalidad: Peruana"
            txtGlosa.text = it.asunto
        }

        return view
    }

    companion object {
        fun newInstance(operacion: Operacion): DetalleOperacionFragment {
            val fragment = DetalleOperacionFragment()
            val args = Bundle()
            args.putSerializable("operacion", operacion)
            fragment.arguments = args
            return fragment
        }
    }
}
