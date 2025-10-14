package com.upc.appgestiones.ui.lista_completa

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Gestion
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.ui.gestiones.DetalleGestionActivity
import com.upc.appgestiones.ui.gestiones.DetalleGestionFragment
import com.upc.appgestiones.ui.gestiones.GestionesAdapter


class ListaCompletaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GestionesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var txtVacio: TextView

    private var _gestiones: List<Gestion> = emptyList()

    private var _gestionesSource: List<Gestion> = emptyList()


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

        _gestionesSource = Gestion.fetchGestionesFinalizadas()

        cargarGestiones(Gestion.fetchGestionesFinalizadas())

        //Accion del buscador
        val edtBuscar: TextInputEditText = view.findViewById(R.id.edtBuscarGestion);
        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString().trim()
                _gestiones = _gestionesSource.filter { gestion ->
                    val nombreCliente:String = buildString {
                        append(gestion.operacionNavigation!!.clienteNavigation.nombres)
                        append(" ")
                        append(gestion.operacionNavigation!!.clienteNavigation.apellidos)
                    }
                    nombreCliente.contains(texto, ignoreCase = true)
                }
                cargarGestiones(_gestiones)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }
    private fun cargarGestiones(lista:List<Gestion>) {
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
