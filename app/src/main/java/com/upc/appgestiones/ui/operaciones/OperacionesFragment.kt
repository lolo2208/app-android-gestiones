package com.upc.appgestiones.ui.operaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Operacion

class OperacionesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OperacionesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var txtVacio: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_operaciones, container, false)

        recyclerView = view.findViewById(R.id.recyclerOperaciones)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        txtVacio = view.findViewById(R.id.txtVacio)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OperacionesAdapter(emptyList()) { operacion ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, DetalleOperacionFragment.newInstance(operacion))
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        cargarOperaciones()

        swipeRefreshLayout.setOnRefreshListener {
            cargarOperaciones()
        }

        return view
    }

    private fun cargarOperaciones() {
        val lista = Operacion.fetchOperaciones()

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
