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
import com.upc.appgestiones.core.data.repository.OperacionRepository

class OperacionesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OperacionesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var txtVacio: TextView
    private var _operaciones: List<Operacion> = emptyList()
    private var _operacionesSrc: List<Operacion> = emptyList()

    private lateinit var edtBuscar: TextInputEditText
    private lateinit var repository: OperacionRepository
    private val operacionViewmodel: OperacionViewmodel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_operaciones, container, false)

        repository = OperacionRepository(requireContext())

        recyclerView = view.findViewById(R.id.recyclerOperaciones)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        txtVacio = view.findViewById(R.id.txtVacio)
        edtBuscar = view.findViewById(R.id.edtBuscar)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OperacionesAdapter(emptyList()) { operacion ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, DetalleOperacionFragment.newInstance(operacion))
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        // Acción Swipe: recargar datos remotos
        swipeRefreshLayout.setOnRefreshListener {
            cargarOperacionesRemotas()
        }

        // Accion del buscador
        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString().trim()
                _operaciones = _operacionesSrc.filter { operacion ->
                    val nombreCliente = "${operacion.clienteNavigation.nombres} ${operacion.clienteNavigation.apellidos}"
                    nombreCliente.contains(texto, ignoreCase = true)
                }
                cargarOperacionesLocal(_operaciones)
            }
        })

        // Cargar por primera vez
        cargarOperacionesRemotas()
        return view
    }

    private fun cargarOperacionesRemotas() {
        swipeRefreshLayout.isRefreshing = true
        repository.listarOperaciones { lista, error ->
            swipeRefreshLayout.isRefreshing = false
            if (error != null) {
                txtVacio.text = "Error al cargar datos: ${error.message}"
                txtVacio.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                return@listarOperaciones
            }
            _operacionesSrc = lista ?: emptyList()
            _operaciones = _operacionesSrc
            cargarOperacionesLocal(_operaciones)
        }
    }

    private fun cargarOperacionesLocal(lista: List<Operacion>) {
        if (lista.isEmpty()) {
            txtVacio.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtVacio.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.actualizar(lista)
        }
    }
}