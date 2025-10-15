package com.upc.appgestiones.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import android.widget.LinearLayout
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.EstadoOperacion
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.core.data.repository.OperacionRepository
import com.upc.appgestiones.ui.gestiones.DetalleGestionActivity
import com.upc.appgestiones.ui.login.LoginActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BienvenidaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BienvenidaFragment : Fragment() {
    private lateinit var tvPendiente: TextView
    private lateinit var tvRealizados: TextView
    private lateinit var btnPendiente: LinearLayout
    private lateinit var btnRealizadas: LinearLayout

    private lateinit var repository: OperacionRepository
    private var listaOperaciones: List<Operacion> = emptyList()

    private lateinit var btnLogout: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bienvenida, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPendiente = view.findViewById(R.id.tvPendientes)
        tvRealizados = view.findViewById(R.id.tvRealizadas)
        btnPendiente = view.findViewById(R.id.btnPendientes)
        btnRealizadas = view.findViewById(R.id.btnRealizadas)
        btnLogout = view.findViewById(R.id.btnLogout)

        repository = OperacionRepository(requireContext())

        btnLogout.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("AppGestionesPrefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        cargarOperaciones()
    }

    private fun cargarOperaciones() {
        repository.listarOperacionesPorUsuario { lista, error ->
            if (error != null) {
                Toast.makeText(
                    requireContext(),
                    "Error al obtener operaciones: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                return@listarOperacionesPorUsuario
            }

            listaOperaciones = lista ?: emptyList()

            val pendientes = listaOperaciones.count { it.estado != EstadoOperacion.FINALIZADA }
            val realizadas = listaOperaciones.count { it.estado == EstadoOperacion.FINALIZADA }

            tvPendiente.text = pendientes.toString()
            tvRealizados.text = realizadas.toString()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BienvenidaFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}