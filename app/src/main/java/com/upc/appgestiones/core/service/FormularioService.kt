package com.upc.appgestiones.core.service

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.upc.appgestiones.core.data.model.CampoFormulario
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.core.data.model.TipoCampo
import com.upc.appgestiones.core.data.model.TipoOperacion

class FormularioService(private val context: Context) {

    fun construirPlantilla(operacion : Operacion) : List<CampoFormulario> {
        var listaCampos = CampoFormulario.fetchCampos()

        return when (operacion.tipo) {
            TipoOperacion.VERIFICACION -> {
                listaCampos.filter { it.tipoFormulario == "VERIFICACION" }
            }
            TipoOperacion.COBRANZA -> {
                listaCampos.filter { it.tipoFormulario == "COBRANZA" }
            }
            else -> emptyList()
        }
    }

    fun construirFormulario(campos: List<CampoFormulario>): Map<String, View> {
        val viewsMap = mutableMapOf<String, View>()

        campos.forEach { campo ->
            val view: View = when (campo.tipoCampo) {
                TipoCampo.TEXT -> {
                    val editText = EditText(context).apply {
                        hint = campo.etiqueta
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    editText
                }
                TipoCampo.SELECT -> {
                    val spinner = Spinner(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        val opciones = obtenerOpciones(campo.nombreCatalogo)
                        adapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            opciones
                        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                    }
                    spinner
                }
                TipoCampo.FOTO -> {
                    val imageView = ImageView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            200, // ancho
                            200  // alto
                        )
                        setBackgroundResource(android.R.color.darker_gray)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        contentDescription = campo.etiqueta
                    }
                    imageView
                }
            }

            viewsMap[campo.nombreCampo] = view
        }

        return viewsMap
    }

    private fun obtenerOpciones(nombreCatalogo: String?): List<String> {
        return when (nombreCatalogo) {
            "EstadoPago" -> listOf("Pagado", "Pendiente", "Retrasado")
            "TipoNegocio" -> listOf("Retail", "Mayorista", "Servicios")
            else -> listOf("Opción 1", "Opción 2")
        }
    }
}