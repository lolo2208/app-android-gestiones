package com.upc.appgestiones.ui.formulario

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.CampoFormulario
import com.upc.appgestiones.core.data.model.Catalogo
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.core.data.model.TipoCampo
import java.io.File

class FormularioActivity : AppCompatActivity() {

    private lateinit var containerFormulario: LinearLayout
    private var operacionId: Int = -1

    private val respuestas = mutableMapOf<String, Any?>()

    private var currentPhotoCampo: String? = null
    private var currentPhotoUri: Uri? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && currentPhotoCampo != null && currentPhotoUri != null) {
                val campo = currentPhotoCampo!!
                val imageView = respuestas[campo] as? ImageView
                if (imageView != null) {
                    imageView.setImageURI(currentPhotoUri)
                    imageView.visibility = ImageView.VISIBLE
                    // 👉 Guardamos la URI local como la respuesta
                    imageView.tag = currentPhotoUri.toString()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)

        containerFormulario = findViewById(R.id.containerFormulario)
        val btnGuardar: Button = findViewById(R.id.btnEnviarFormulario)

        operacionId = intent.getIntExtra("ID_OPERACION", -1)
        Log.d("FormularioActivity", "Recibiendo ID de Operación: $operacionId")

        if (operacionId != -1) {
            val operacion = fetchOperacionById(operacionId)

            if (operacion != null) {
                val tipoOperacion = operacion.tipo

                val campos = CampoFormulario.fetchCampos()
                    .filter { it.tipoFormulario == tipoOperacion.toString() }

                if (campos.isNotEmpty()) {
                    crearCamposDinamicos(campos)
                }

                btnGuardar.setOnClickListener {
                    val resultados = mutableMapOf<String, Any?>()

                    for ((campo, view) in respuestas) {
                        when (view) {
                            is EditText -> resultados[campo] = view.text.toString().trim()
                            is Spinner -> resultados[campo] = view.selectedItem?.toString()
                            is ImageView -> resultados[campo] = view.tag // 👉 ahora es la URI string
                        }
                    }

                    Log.d("FormularioActivity", "Respuestas: $resultados")
                    Toast.makeText(this, "Gestión guardada ✅", Toast.LENGTH_SHORT).show()

                    val resultIntent = Intent().apply {
                        putExtra("ID_OPERACION_RESUELTA", operacionId)
                        putExtra("RESPUESTAS", HashMap(resultados))
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            } else {
                Log.e("FormularioActivity", "Operación no encontrada para el ID: $operacionId")
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun crearCamposDinamicos(campos: List<CampoFormulario>) {
        containerFormulario.removeAllViews()

        for (campo in campos) {
            val label = TextView(this).apply {
                text = campo.etiqueta
                textSize = 16f
                setTypeface(typeface, Typeface.BOLD)
                setPadding(0, 24, 0, 8)
            }
            containerFormulario.addView(label)

            when (campo.tipoCampo) {
                TipoCampo.TEXT -> {
                    val editText = EditText(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 32 }
                        hint = "Ingrese ${campo.etiqueta}"
                    }
                    containerFormulario.addView(editText)
                    respuestas[campo.nombreCampo] = editText
                }

                TipoCampo.SELECT -> {
                    val catalogo = Catalogo.fetchCatalogos()
                        .find { it.nombreCatalogo == campo.nombreCatalogo }
                    val items = catalogo?.detallesCatalogo?.map { it.codigoDetalle } ?: emptyList()

                    val spinner = Spinner(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 32 }
                    }
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        items
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                    containerFormulario.addView(spinner)
                    respuestas[campo.nombreCampo] = spinner
                }

                TipoCampo.FOTO -> {
                    val button = Button(this).apply {
                        text = "Subir ${campo.etiqueta}"
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 16 }
                    }
                    containerFormulario.addView(button)

                    val imageView = ImageView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            600
                        ).apply { bottomMargin = 32 }
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        visibility = ImageView.GONE
                    }
                    containerFormulario.addView(imageView)

                    respuestas[campo.nombreCampo] = imageView

                    button.setOnClickListener {
                        currentPhotoCampo = campo.nombreCampo
                        currentPhotoUri = createImageUri(campo.nombreCampo)
                        takePictureLauncher.launch(currentPhotoUri!!)
                    }
                }
            }
        }
    }

    private fun createImageUri(nombreCampo: String): Uri {
        val imageFile = File.createTempFile(
            "IMG_${nombreCampo}_",
            ".jpg",
            cacheDir
        )
        return FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            imageFile
        )
    }

    private fun fetchOperacionById(id: Int): Operacion? {
        val listaDeOperaciones = Operacion.fetchOperaciones()
        return listaDeOperaciones.find { it.id == id }
    }
}
