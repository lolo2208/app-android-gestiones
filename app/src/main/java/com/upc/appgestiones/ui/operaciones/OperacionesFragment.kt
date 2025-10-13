package com.upc.appgestiones.ui.operaciones

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Operacion

class OperacionesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OperacionesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var txtVacio: TextView
    private var _operaciones: List<Operacion> = emptyList()
    private var _operacionesSrc: List<Operacion> = emptyList()

    private val operacionViewmodel: OperacionViewmodel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_operaciones, container, false)

        recyclerView = view.findViewById(R.id.recyclerOperaciones)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        txtVacio = view.findViewById(R.id.txtVacio)

        //Iniciar recycler view
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OperacionesAdapter(emptyList()) { operacion ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, DetalleOperacionFragment.newInstance(operacion))
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        //Accion Swipe
        swipeRefreshLayout.setOnRefreshListener {
            operacionViewmodel.refreshOperaciones()
            _operacionesSrc = operacionViewmodel.operaciones.value ?: emptyList()
            _operaciones = _operacionesSrc
            cargarOperaciones(_operaciones)
        }

        //Observar cambios de las operaciones en viewmodel
        operacionViewmodel.operaciones.observe(viewLifecycleOwner) { operaciones ->
            _operacionesSrc = operaciones
            _operaciones = _operacionesSrc
            cargarOperaciones(_operaciones)
        }

        //Accion del buscador
        val edtBuscar: TextInputEditText = view.findViewById(R.id.edtBuscar);
        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString().trim()
                _operaciones = _operacionesSrc.filter { operacion ->
                    val nombreCliente:String = buildString {
                        append(operacion.clienteNavigation.nombres)
                        append(" ")
                        append(operacion.clienteNavigation.apellidos)
                    }
                    nombreCliente.contains(texto, ignoreCase = true)
                }
                cargarOperaciones(_operaciones)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        return view
    }

    //Llenar recyclerview
    private fun cargarOperaciones(lista: List<Operacion>) {
        if (lista.isEmpty()) {
            txtVacio.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtVacio.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.actualizar(lista)
        }
        swipeRefreshLayout.isRefreshing = false
    }
}