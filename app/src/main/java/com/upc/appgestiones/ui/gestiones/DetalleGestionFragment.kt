package com.upc.appgestiones.ui.gestiones

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Gestion
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.ui.operaciones.DetalleOperacionFragment


class DetalleGestionFragment : Fragment() {
    private var gestion: Gestion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestion = arguments?.getSerializable("gestion") as? Gestion
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_gestion, container, false)


        val txtIdGestion = view.findViewById<TextView>(R.id.txtIdGestion)
        val txtIdOperacion = view.findViewById<TextView>(R.id.txtIdOperacion)
        val txtFechaRegistro = view.findViewById<TextView>(R.id.txtFechaRegistro)
        val txtEstadoOperacion = view.findViewById<TextView>(R.id.txtEstadoOperacion)
        val txtFormulario = view.findViewById<TextView>(R.id.txtFormulario)
        val imgEvidencia = view.findViewById<ImageView>(R.id.imgEvidencia)
        val btnPlayAudio = view.findViewById<ImageView>(R.id.btnPlayAudio)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtDireccion = view.findViewById<TextView>(R.id.txtDireccion)
        val txtDocumento = view.findViewById<TextView>(R.id.txtDocumento)


        gestion?.let {
            txtIdGestion.text = "ID Gestión: ${it.idGestion}"
            txtIdOperacion.text = "ID Operación: ${it.idOperacion}"
            txtFechaRegistro.text = "Fecha: ${it.fechaRegistro}"
            txtEstadoOperacion.text = "Estado: ${it.operacionNavigation.estado}"
            txtFormulario.text = "Formulario: ${it.formularioJson}"
            txtNombre.text = "Cliente: ${it.operacionNavigation.clienteNavigation.nombres} ${it.operacionNavigation.clienteNavigation.apellidos}"
            txtDireccion.text = "Dirección: ${it.operacionNavigation.direccionNavigation.calle} ${it.operacionNavigation.direccionNavigation.numero}, ${it.operacionNavigation.direccionNavigation.ciudad}"
            txtDocumento.text = "Documento: ${it.operacionNavigation.clienteNavigation.documento}"

            if (!it.urlGrabacionVoz.isNullOrEmpty()) {
                btnPlayAudio.visibility = ImageView.VISIBLE
                var mediaPlayer: MediaPlayer? = null
                btnPlayAudio.setOnClickListener { _ ->
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(it.urlGrabacionVoz)
                        prepare()
                        start()
                    }
                }
            }


            if (!it.urlFotoEvidencia.isNullOrEmpty()) {
                Glide.with(this).load(it.urlFotoEvidencia).into(imgEvidencia)
            }


        }
        return view
    }

    companion object {
        fun newInstance(gestion: Gestion): DetalleGestionFragment {
            val fragment = DetalleGestionFragment()
            val args = Bundle()
            args.putSerializable("gestion", gestion)
            fragment.arguments = args
            return fragment
        }
    }
}