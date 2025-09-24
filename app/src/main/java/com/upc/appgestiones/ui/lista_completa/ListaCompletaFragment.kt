package com.upc.appgestiones.ui.lista_completa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.upc.appgestiones.R
import com.upc.appgestiones.ui.gestiones.DetalleGestionActivity
import com.upc.appgestiones.ui.gestiones.DetalleGestionFragment
import com.upc.appgestiones.ui.gestiones.GestionesAdapter


class ListaCompletaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GestionesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var txtVacio: TextView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_completa, container, false)

        recyclerView = view.findViewById(R.id.recyclerGestiones)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        txtVacio = view.findViewById(R.id.txtVacio)

        recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        adapter = GestionesAdapter(emptyList()) { gestion ->
            parentFragmentManager.beginTransaction().replace(R.id.frameLayout,
                DetalleGestionFragment.newInstance(gestion))
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
        cargarGestiones()
        swipeRefreshLayout.setOnRefreshListener {
            cargarGestiones()

        }
        return view
    }
    private fun cargarGestiones() {
        val lista = com.upc.appgestiones.core.data.model.Gestion.fetchGestionesFinalizadas()

        if (lista.isEmpty()) {
            txtVacio.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtVacio.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter = GestionesAdapter(lista) { gestion ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, DetalleGestionFragment.newInstance(gestion))
                    .addToBackStack(null)
                    .commit()
            }
            recyclerView.adapter = adapter
        }
        swipeRefreshLayout.isRefreshing = false
    }
}
