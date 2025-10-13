package com.upc.appgestiones.ui.formulario

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.CampoFormulario
import com.upc.appgestiones.core.data.model.Catalogo
import com.upc.appgestiones.core.data.model.Gestion
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.core.data.model.TipoCampo
import java.io.File
import com.google.gson.Gson
import com.jakewharton.threetenabp.AndroidThreeTen
import com.upc.appgestiones.core.audio.AudioRecorderService
import org.threeten.bp.LocalDateTime

class FormularioActivity : AppCompatActivity() {

    private lateinit var containerFormulario: LinearLayout
    private var operacionId: Int = -1

    private val respuestas = mutableMapOf<String, Any?>()

    private var currentPhotoCampo: String? = null
    private var currentPhotoUri: Uri? = null

    private lateinit var audioRecorder: AudioRecorderService
    private var audioPath: String? = null

    private lateinit var imgEvidencia: ImageView

    private var evidenciaUri: Uri? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && currentPhotoCampo != null && currentPhotoUri != null) {
                val campo = currentPhotoCampo!!
                val imageView = respuestas[campo] as? ImageView
                if (imageView != null) {
                    imageView.setImageURI(currentPhotoUri)
                    imageView.visibility = ImageView.VISIBLE
                    imageView.tag = currentPhotoUri.toString()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)

        AndroidThreeTen.init(this)

        containerFormulario = findViewById(R.id.containerFormulario)
        val btnGuardar: Button = findViewById(R.id.btnEnviarFormulario)

        operacionId = intent.getIntExtra("ID_OPERACION", -1)
        Log.d("FormularioActivity", "Recibiendo ID de Operación: $operacionId")

        audioRecorder = AudioRecorderService(this)
        if (audioRecorder.verificarAccesoAlMicro()) {
            audioRecorder.iniciarGrabacion()
            audioPath = audioRecorder.obtenerPathGrabacion()
            Log.d("FormularioActivity", "Grabación iniciada en: $audioPath")
        } else {
            Log.e("FormularioActivity", "No se tienen permisos de grabación")
        }


        if (operacionId != -1) {
            val operacion = fetchOperacionById(operacionId)

            if (operacion != null) {
                agregarCamposFijos()

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
                            is ImageView -> resultados[campo] = view.tag
                        }
                    }

                    val operacion = fetchOperacionById(operacionId)!!
                    val gestion = Gestion(
                        idGestion = (0..999999).random(),
                        idOperacion = operacion.idOperacion,
                        fechaRegistro = LocalDateTime.now().toString(),
                        formularioJson = Gson().toJson(resultados),
                        urlGrabacionVoz = audioPath,
                        urlFotoEvidencia = evidenciaUri?.toString(),
                        respuesta = "",
                        operacionNavigation = operacion
                    )


                    audioRecorder.finalizarGrabacion()

                    val gson = Gson()
                    val gestionJson = gson.toJson(gestion)

                    val resultIntent = Intent().apply {
                        putExtra("GESTION_JSON", gestionJson)
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

    private fun agregarCamposFijos() {
        //crear spinner de respuesta
        val labelRespuesta = TextView(this).apply {
            text = "Respuesta de la gestión"
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 24, 0, 8)
        }
        containerFormulario.addView(labelRespuesta)

        val catalogo = Catalogo.fetchCatalogos()
            .find { it.nombreCatalogo == "RespuestasGestion" }

        val detalles = catalogo?.detallesCatalogo ?: emptyList()
        val items = detalles.map { it.descripcion }

        val textInputLayoutRespuesta = com.google.android.material.textfield.TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }

            hint = "Selecciona una opción"
            setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
        }

        val spinnerRespuesta = Spinner(this, Spinner.MODE_DROPDOWN).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = ContextCompat.getDrawable(context, R.drawable.bg_spinner_outlined)
            adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
        }

        textInputLayoutRespuesta.addView(spinnerRespuesta)
        containerFormulario.addView(textInputLayoutRespuesta)

        respuestas["respuesta_gestion"] = items[0]

        spinnerRespuesta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                respuestas["respuesta_gestion"] = items[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                respuestas["respuesta_gestion"] = null
            }
        }

        //Crear campo de evidencia
        val labelEvidencia = TextView(this).apply {
            text = "Evidencia de gestión"
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 24, 0, 8)
        }
        containerFormulario.addView(labelEvidencia)

        val buttonFoto = Button(this).apply {
            text = "Subir Evidencia"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }
        containerFormulario.addView(buttonFoto)

        val imageViewEvidencia = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            ).apply { bottomMargin = 32 }
            scaleType = ImageView.ScaleType.CENTER_CROP
            visibility = ImageView.GONE
        }
        containerFormulario.addView(imageViewEvidencia)

        respuestas["foto_evidencia_gestion"] = imageViewEvidencia

        buttonFoto.setOnClickListener {
            currentPhotoCampo = "foto_evidencia_gestion"
            currentPhotoUri = createImageUri("foto_evidencia_gestion")
            takePictureLauncher.launch(currentPhotoUri!!)
        }

        //CRear campo observación
        val labelObservacion = TextView(this).apply {
            text = "Observación"
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 24, 0, 8)
        }
        containerFormulario.addView(labelObservacion)

        val textInputLayoutObservacion = com.google.android.material.textfield.TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }

            hint = "Ingrese su observación"
            setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
        }

        val editTextObservacion = com.google.android.material.textfield.TextInputEditText(textInputLayoutObservacion.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
            maxLines = 5
        }

        textInputLayoutObservacion.addView(editTextObservacion)
        containerFormulario.addView(textInputLayoutObservacion)

        respuestas["observacion_gestion"] = editTextObservacion
    }

    private fun crearCamposDinamicos(campos: List<CampoFormulario>) {
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
                    val textInputLayout = com.google.android.material.textfield.TextInputLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 32 }

                        hint = campo.etiqueta
                        setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
                    }

                    val editText = com.google.android.material.textfield.TextInputEditText(textInputLayout.context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        inputType = android.text.InputType.TYPE_CLASS_TEXT
                    }

                    textInputLayout.addView(editText)

                    containerFormulario.addView(textInputLayout)

                    respuestas[campo.nombreCampo] = editText
                }


                TipoCampo.SELECT -> {
                    val catalogo = Catalogo.fetchCatalogos()
                        .find { it.nombreCatalogo == campo.nombreCatalogo }

                    val detalles = catalogo?.detallesCatalogo ?: emptyList()
                    val items = detalles.map { it.descripcion }

                    val textInputLayout = com.google.android.material.textfield.TextInputLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 32 }

                        hint = campo.etiqueta
                        isHintEnabled = true
                        setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
                    }

                    val spinner = Spinner(this, Spinner.MODE_DROPDOWN).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        background = ContextCompat.getDrawable(context, R.drawable.bg_spinner_outlined)

                        adapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_dropdown_item,
                            items
                        )
                    }

                    val container = FrameLayout(this).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        )
                        setPadding(0, 8, 0, 8)
                        addView(spinner)
                    }

                    textInputLayout.addView(container)
                    containerFormulario.addView(textInputLayout)

                    if (detalles.isNotEmpty()) {
                        respuestas[campo.nombreCampo] = detalles[0].codigoDetalle
                    }

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            respuestas[campo.nombreCampo] = detalles[position].codigoDetalle
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            respuestas[campo.nombreCampo] = null
                        }
                    }
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

                TipoCampo.FECHA -> {
                    val textInputLayout = com.google.android.material.textfield.TextInputLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 32 }

                        hint = campo.etiqueta
                        setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
                        setStartIconDrawable(R.drawable.ic_calendar)
                    }

                    val editText = com.google.android.material.textfield.TextInputEditText(textInputLayout.context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        inputType = android.text.InputType.TYPE_NULL
                        isFocusable = false
                        isClickable = true

                        setOnClickListener {
                            val calendar = java.util.Calendar.getInstance()
                            val datePicker = android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                                    setText(selectedDate)
                                },
                                calendar.get(java.util.Calendar.YEAR),
                                calendar.get(java.util.Calendar.MONTH),
                                calendar.get(java.util.Calendar.DAY_OF_MONTH)
                            )
                            datePicker.show()
                        }
                    }

                    textInputLayout.addView(editText)

                    containerFormulario.addView(textInputLayout)

                    respuestas[campo.nombreCampo] = editText
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
        return listaDeOperaciones.find { it.idOperacion == id }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::audioRecorder.isInitialized) {
            audioRecorder.finalizarGrabacion()
            Log.d("FormularioActivity", "Grabación detenida en onDestroy()")
        }
    }
}
