package com.upc.appgestiones.ui.map

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.EstadoOperacion
import com.upc.appgestiones.core.data.model.Gestion
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.core.services.MapService
import com.upc.appgestiones.core.utils.DateUtil
import com.upc.appgestiones.core.utils.MapUtil
import com.upc.appgestiones.ui.formulario.FormularioActivity
import com.upc.appgestiones.ui.home.BienvenidaViewModel
import com.upc.appgestiones.ui.operaciones.OperacionViewmodel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import kotlin.math.absoluteValue
import kotlin.ranges.contains

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    private val mapViewModel: MapViewModel by activityViewModels()
    private val operacionViewmodel: OperacionViewmodel by activityViewModels()
    private val bienvenidaViewModel: BienvenidaViewModel by activityViewModels()
    private var mapView: MapView? = null
    private var mapService: MapService? = null

    private val formularioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val gestionJson = data?.getStringExtra("GESTION_JSON")

            gestionJson?.let {
                val gestion = Gson().fromJson(it, Gestion::class.java)
                val listaActual = mapViewModel.manejarGestion(gestion)
                bienvenidaViewModel.setOperaciones(listaActual)
                operacionViewmodel.setOperaciones(listaActual)

                val operacionFinalizada = listaActual.find { op -> op.idOperacion == gestion.idOperacion }
                if(operacionFinalizada != null) {
                    val gestionActualizada = gestion.copy(
                        operacionNavigation = operacionFinalizada!!
                    )
                }
            }
        }
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Configuration.getInstance().userAgentValue = requireContext().packageName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)
        mapView!!.setTileSource(TileSourceFactory.MAPNIK)
        mapView!!.setMultiTouchControls(true)
        mapView!!.controller.setZoom(15.0)

        mapService = MapService(requireContext(), mapView!!)

        // Observa operaciones y crea marcadores
        mapViewModel.operaciones.observe(viewLifecycleOwner) { lista ->
            val listaOperaciones = lista.filter { operacion ->
                operacion.estado != EstadoOperacion.FINALIZADA
            }
            mapService?.addOperacionMarkers(
                listaOperaciones,
                onMarkerClick = { operacion ->
                    mapView!!.controller.setCenter(GeoPoint(operacion.direccionNavigation.latitud ?: 0.0, operacion.direccionNavigation.longitud ?: 0.0))
                    showOperacionBottomSheet(operacion)
                },
                onGeoClick = { operacion ->
                    actualizarOperacionEnViewModel(operacion, mapViewModel)
                }
            )
        }

        // Observa ubicación y mueve el mapa
        mapViewModel.miUbicacion.observe(viewLifecycleOwner) { point ->
            //mapView!!.controller.setCenter(point)
        }

        // Obtener ubicación actual
        mapService?.getCurrentLocation(
            onSuccess = { geoPoint -> mapViewModel.setUbicacion(geoPoint) },
            onFailure = { e -> println("Error ubicación: ${e.message}") }
        )

        // Iniciar actualizaciones en tiempo real
        mapService?.startLocationUpdates { geoPoint ->
            mapViewModel.setUbicacion(geoPoint)
        }

        val btnRuta = view.findViewById<Button>(R.id.btnRuta)
        btnRuta.setOnClickListener {
            mapService?.getCurrentLocation(
                onSuccess = { ubicacionActual ->
                    val operacionesOrdenadas = MapUtil.ordenarOperacionesPorRuta(
                        mapViewModel.operaciones.value ?: emptyList(),
                        ubicacionActual
                    )
                    mapViewModel.setOperaciones(operacionesOrdenadas)

                    val puntos: MutableList<GeoPoint> = mutableListOf()
                    puntos.add(ubicacionActual)
                    operacionesOrdenadas.forEach { operacion ->
                        val lat = operacion.direccionNavigation.latitud
                        val lon = operacion.direccionNavigation.longitud
                        if (lat != null && lon != null) {
                            puntos.add(GeoPoint(lat, lon))
                        }
                    }

                    mapService?.drawRutaOptima(puntos)
                },
                onFailure = { e ->
                    Toast.makeText(context, "No se pudo obtener la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showOperacionBottomSheet(operacion: Operacion) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_operacion, null)

        val txtAsunto = view.findViewById<TextView>(R.id.txtAsunto)
        val txtCliente = view.findViewById<TextView>(R.id.txtCliente)
        val txtDireccion = view.findViewById<TextView>(R.id.txtDireccion)
        val txtUbigeo = view.findViewById<TextView>(R.id.txtUbigeo)
        val txtReferencia = view.findViewById<TextView>(R.id.txtReferencia)
        val txtTiempo = view.findViewById<TextView>(R.id.txtTiempo)
        val btnAccion = view.findViewById<Button>(R.id.btnAccion)
        val btnGeo = view.findViewById<Button>(R.id.btnGeolocalizar)

        val diasRestantes = DateUtil.diferenciaDeFechaActual(operacion.fechaVencimiento)
        when {
            diasRestantes < 0 -> {
                txtTiempo.text = buildString {
                    append("Días de atraso: ")
                    append(diasRestantes.absoluteValue)
                }
                txtTiempo.setTextColor(ContextCompat.getColor(context, R.color.card_red))
            }

            diasRestantes in 0..3 -> {
                txtTiempo.text = buildString {
                    append("Faltan ")
                    append(diasRestantes)
                    append(" día(s) para vencer")
                }
                txtTiempo.setTextColor(ContextCompat.getColor(context, R.color.card_orange))
            }

            else -> {
                txtTiempo.text = "Operación al día"
                txtTiempo.setTextColor(ContextCompat.getColor(context, R.color.card_green))
            }
        }

        txtAsunto.text = operacion.asunto
        txtCliente.text = "Cliente: ${operacion.clienteNavigation.nombres} ${operacion.clienteNavigation.apellidos}"
        txtDireccion.text = "Dirección: ${operacion.direccionNavigation.calle} ${operacion.direccionNavigation.numero}"
        txtUbigeo.text = "Ciudad: ${operacion.direccionNavigation.ciudad}, ${operacion.direccionNavigation.provincia}"
        txtReferencia.text = "Referencia: ${operacion.direccionNavigation.referencia ?: "Sin referencia"}"

        // Cambiar texto y color según el estado de la operación
        when (operacion.estado) {
            EstadoOperacion.PENDIENTE -> {
                btnAccion.text = "EN CAMINO"
                btnAccion.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.custom_orange))
            }
            EstadoOperacion.EN_RUTA -> {
                btnAccion.text = "GESTIONAR"
                btnAccion.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.custom_green))
            }
            else -> {
                btnAccion.text = "Ver detalle"
                btnAccion.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.custom_primary))
            }
        }

        btnAccion.setOnClickListener {
            when (operacion.estado) {
                EstadoOperacion.PENDIENTE -> {
                    val operacionesActuales = operacionViewmodel.operaciones.value
                    if(operacionesActuales?.any { operacion -> operacion.estado == EstadoOperacion.EN_RUTA }
                            ?: true) {
                        Toast.makeText(requireContext(), "Ya existe una operación en ruta", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(requireContext(), "La operación ${operacion.asunto} está en camino", Toast.LENGTH_SHORT).show()
                        val operacionActualizada = operacion.copy(estado = EstadoOperacion.EN_RUTA)
                        actualizarOperacionEnViewModel(operacionActualizada, mapViewModel)
                        bottomSheet.dismiss()
                    }

                }
                EstadoOperacion.EN_RUTA -> {
                    val intent = Intent(requireContext(), FormularioActivity::class.java)
                    Log.d("MapFragment", "ID operacion = ${operacion.idOperacion}")
                    intent.putExtra("ID_OPERACION", operacion.idOperacion)
                    formularioLauncher.launch(intent)
                }
                else -> {
                    Toast.makeText(requireContext(), "Detalle de ${operacion.asunto}", Toast.LENGTH_SHORT).show()
                }
            }

            bottomSheet.dismiss()
        }

        btnGeo.setOnClickListener {
            mapService?.updateOperacionWithCurrentLocation(
                operacion,
                onSuccess = { operacionActualizada ->
                    actualizarOperacionEnViewModel(operacionActualizada, mapViewModel)
                    Toast.makeText(context, "Ubicación actualizada correctamente", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(context, "Error al actualizar ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    private fun actualizarOperacionEnViewModel(operacionActualizada: Operacion, mapViewModel: MapViewModel) {
        val listaActual = mapViewModel.operaciones.value ?: emptyList()
        val listaActualizada = listaActual.map {
            if (it.idOperacion == operacionActualizada.idOperacion) operacionActualizada else it
        }
        mapViewModel.setOperaciones(listaActualizada)
        bienvenidaViewModel.setOperaciones(listaActualizada)
        operacionViewmodel.setOperaciones(listaActualizada)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapService?.stopLocationUpdates()
    }
}