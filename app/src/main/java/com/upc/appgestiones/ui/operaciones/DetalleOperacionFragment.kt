package com.upc.appgestiones.ui.operaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Contacto
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.ui.contacto.ContactosAdapter

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


        val txtNombre = view.findViewById<TextView>(R.id.txtNombreCliente)
        val txtDocumento = view.findViewById<TextView>(R.id.txtDocumentoCliente)
        val txtDireccion = view.findViewById<TextView>(R.id.txtCalle)
        val txtCiudad = view.findViewById<TextView>(R.id.txtCiudad)
        val txtReferencia = view.findViewById<TextView>(R.id.txtReferencia)
        val recyclerContactos = view.findViewById<RecyclerView>(R.id.recyclerContactos)

        operacion?.let {


            txtNombre.text = "${it.clienteNavigation.nombres} ${it.clienteNavigation.apellidos}"
            txtDocumento.text = "DNI: ${it.clienteNavigation.documento}"
            txtDireccion.text = "${it.direccionNavigation.calle} ${it.direccionNavigation.numero}"
            txtCiudad.text = "${it.direccionNavigation.ciudad}"
            txtReferencia.text = "${it.direccionNavigation.referencia}"

            val contactos = it.clienteNavigation.contactos

            recyclerContactos.layoutManager = LinearLayoutManager(requireContext())
            recyclerContactos.adapter = ContactosAdapter(contactos)
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