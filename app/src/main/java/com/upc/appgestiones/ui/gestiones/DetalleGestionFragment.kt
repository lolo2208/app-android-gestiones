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
import android.content.Context
import android.graphics.Typeface
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.upc.appgestiones.core.data.model.CampoFormulario
import com.upc.appgestiones.core.data.model.Catalogo
import com.upc.appgestiones.core.data.model.TipoCampo
import com.upc.appgestiones.core.data.model.TipoOperacion
import org.json.JSONObject


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


        val txtIdOperacion = view.findViewById<TextView>(R.id.txtIdOperacion)
        val txtFechaRegistro = view.findViewById<TextView>(R.id.txtFechaRegistro)
        val txtEstadoOperacion = view.findViewById<TextView>(R.id.txtEstadoOperacion)
        val txtFormulario = view.findViewById<TextView>(R.id.txtFormulario)
        val imgEvidencia = view.findViewById<ImageView>(R.id.imgEvidencia)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtDireccion = view.findViewById<TextView>(R.id.txtDireccion)
        val txtDocumento = view.findViewById<TextView>(R.id.txtDocumento)
        val txtRespuesta = view.findViewById<TextView>(R.id.txtRespuesta)


        gestion?.let {
            txtRespuesta.text = "${obtenerDescripcionCatalogo("RespuestasGestion", it.respuesta)}"
            txtIdOperacion.text = "ID Operación: ${it.idOperacion}"
            txtFechaRegistro.text = "Fecha: ${it.fechaRegistro.replace('T', ' ')}"
            txtEstadoOperacion.text = "Estado: ${it.operacionNavigation!!.estado}"
            txtFormulario.text = "Formulario: ${it.formularioJson}"
            txtNombre.text = "Cliente: ${it.operacionNavigation!!.clienteNavigation.nombres} ${it.operacionNavigation!!.clienteNavigation.apellidos}"
            txtDireccion.text = "Dirección: ${it.operacionNavigation!!.direccionNavigation.calle} ${it.operacionNavigation!!.direccionNavigation.numero}, ${it.operacionNavigation.direccionNavigation.ciudad}"
            txtDocumento.text = "Documento: ${it.operacionNavigation!!.clienteNavigation.documento}"
            txtIdOperacion.text = "ID Operacion: ${it.idOperacion}"


            if (!it.urlFotoEvidencia.isNullOrEmpty()) {
                Glide.with(this).load(it.urlFotoEvidencia).into(imgEvidencia)
            }

            replaceFormularioTextViewWithCampos(view, it, requireContext())
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

    fun obtenerDescripcionCatalogo(nombreCatalogo: String?, codigoBusqueda: String?): String? {
        if (nombreCatalogo == null || codigoBusqueda == null) return null
        val catalogs = Catalogo.fetchCatalogos()
        val c = catalogs.find { it.nombreCatalogo.equals(nombreCatalogo, ignoreCase = true) }

        return if(c != null) {
            val r = c.detallesCatalogo.find { it ->
                it.codigoDetalle == codigoBusqueda
            }
            if(r != null) {
                r.descripcion
            }else {
                codigoBusqueda
            }
        }else {
            codigoBusqueda
        }
    }

    fun replaceFormularioTextViewWithCampos(rootView: View, gestion: Gestion, context: Context) {
        val txtFormulario = rootView.findViewById<TextView>(R.id.txtFormulario) ?: return
        val parent = txtFormulario.parent as? ViewGroup ?: return

        val json = try { JSONObject(gestion.formularioJson) } catch (e: Exception) { JSONObject() }

        val tipoFormulario = when (gestion.operacionNavigation!!.tipo) {
            TipoOperacion.COBRANZA -> "COBRANZA"
            TipoOperacion.VERIFICACION -> "VERIFICACION"
            else -> "COBRANZA"
        }

        val allCampos = CampoFormulario.fetchCampos()
        val camposDelFormulario = allCampos.filter { it.tipoFormulario == tipoFormulario }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        camposDelFormulario.forEach { campo ->
            val key = campo.nombreCampo
            val tipo = campo.tipoCampo
            val valor = if (json.has(key)) json.optString(key, "") else ""

            val textInputLayout = com.google.android.material.textfield.TextInputLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 }

                // 🔹 estilo idéntico al XML de login
                hint = campo.etiqueta
                setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
                isHintEnabled = true
                boxStrokeColor = ContextCompat.getColor(context, R.color.custom_primary)
                setBoxCornerRadii(12f, 12f, 12f, 12f)
            }

            when (tipo) {
                TipoCampo.TEXT, TipoCampo.SELECT, TipoCampo.FECHA -> {
                    val textInputLayout = com.google.android.material.textfield.TextInputLayout(context, null).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 24 }

                        setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
                        boxStrokeColor = ContextCompat.getColor(context, R.color.custom_primary)
                        hint = campo.etiqueta
                        isHintEnabled = true
                        setStartIconTintList(ContextCompat.getColorStateList(context, R.color.custom_primary))
                    }

                    val editText = com.google.android.material.textfield.TextInputEditText(textInputLayout.context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        setText(valor.ifBlank { "—" })
                        isEnabled = false
                        isFocusable = false
                        isClickable = false
                        setTextColor(ContextCompat.getColor(context, R.color.black))
                        textSize = 16f
                    }

                    textInputLayout.addView(editText)
                    container.addView(textInputLayout)
                }

                TipoCampo.FOTO -> {
                    val imageView = ImageView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            600
                        ).apply { bottomMargin = 32 }
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    if (valor.isNotBlank()) {
                        Glide.with(context).load(valor).into(imageView)
                    } else {
                        imageView.setImageResource(android.R.color.darker_gray)
                    }
                    container.addView(imageView)
                }
            }
        }

        val index = parent.indexOfChild(txtFormulario)
        parent.removeViewAt(index)
        parent.addView(container, index)
    }
}