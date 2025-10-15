package com.upc.appgestiones.ui.formulario

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.jakewharton.threetenabp.AndroidThreeTen
import com.upc.appgestiones.R
import com.upc.appgestiones.core.audio.AudioRecorderService
import com.upc.appgestiones.core.data.model.*
import com.upc.appgestiones.core.data.repository.DetalleCatalogoRepository
import com.upc.appgestiones.core.data.repository.GestionRepository
import com.upc.appgestiones.core.sqlite.dao.RespaldoDAO
import org.threeten.bp.LocalDateTime
import java.io.File

class FormularioActivity : AppCompatActivity() {

    private lateinit var containerFormulario: LinearLayout
    private var operacionId: Int = -1

    private val respuestas = mutableMapOf<String, Any?>()

    private var currentPhotoCampo: String? = null
    private var currentPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null

    private lateinit var audioRecorder: AudioRecorderService
    private var audioPath: String? = null

    private lateinit var detalleCatalogoRepo: DetalleCatalogoRepository

    private var listaDetallesCatalogo: List<DetalleCatalogo> = emptyList()

    private lateinit var respaldoDAO: RespaldoDAO

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && currentPhotoCampo != null && currentPhotoUri != null) {
                val campo = currentPhotoCampo!!
                respuestas[campo] = currentPhotoPath
                Log.d("FormularioActivity", "Foto guardada en: $currentPhotoPath")

                // Mostrar la imagen en el ImageView correspondiente
                val imageView = containerFormulario.findViewWithTag<ImageView>(campo)
                imageView?.apply {
                    setImageURI(currentPhotoUri)
                    visibility = View.VISIBLE
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)
        AndroidThreeTen.init(this)

        containerFormulario = findViewById(R.id.containerFormulario)
        val btnEnviarFormulario: Button = findViewById(R.id.btnEnviarFormulario)

        detalleCatalogoRepo = DetalleCatalogoRepository(this)

        respaldoDAO = RespaldoDAO(this)

        val operacionJson = intent.getStringExtra("OPERACION_JSON")
        val operacion = operacionJson?.let { Gson().fromJson(it, Operacion::class.java) }

        if (operacion == null) {
            Log.e("FormularioActivity", "No se recibió una operación válida")
            finish()
            return
        }

        operacionId = operacion.idOperacion

        // Iniciar grabadora de audio
        audioRecorder = AudioRecorderService(this)
        if (audioRecorder.verificarAccesoAlMicro()) {
            audioRecorder.iniciarGrabacion()
            audioPath = audioRecorder.obtenerPathGrabacion()
            Log.d("FormularioActivity", "Grabación iniciada en: $audioPath")
        } else {
            Log.e("FormularioActivity", "No se tienen permisos de grabación")
        }

        if (operacionId > 0) {
            detalleCatalogoRepo.getDetalleCatalogos(
                onSuccess = { detalles ->
                    listaDetallesCatalogo = detalles

                    if (operacion != null) {
                        agregarCamposFijos()

                        val tipoOperacion = operacion.tipo
                        val campos = CampoFormulario.fetchCampos()
                            .filter { it.tipoFormulario == tipoOperacion.toString() }

                        if (campos.isNotEmpty()) crearCamposDinamicos(campos)
                    } else {
                        Log.e("FormularioActivity", "Operación no encontrada para el ID: $operacionId")
                    }
                },
                onError = { error ->
                    Toast.makeText(this, "Error al obtener catálogos: ${error.message}", Toast.LENGTH_LONG).show()
                    agregarCamposFijos() // Crea al menos los fijos
                }
            )
        }

        btnEnviarFormulario.setOnClickListener {
            val resultados = mutableMapOf<String, Any?>()
            for ((campo, valor) in respuestas) {
                when (valor) {
                    is EditText -> resultados[campo] = valor.text.toString().trim()
                    is Spinner -> resultados[campo] = valor.selectedItem?.toString()
                    is String -> resultados[campo] = valor
                }
            }

            audioRecorder.finalizarGrabacion()

            val gestion = crearGestionDesdeFormulario(
                idOperacion = operacionId,
                respuestasFormulario = resultados,
                audioPath = audioPath
            )

            try {
                if (!audioPath.isNullOrEmpty()) {
                    respaldoDAO.insertar(operacionId, "audio_gestion", audioPath!!)
                    Log.d("FormularioActivity", "Audio respaldado en SQLite: $audioPath")
                }

                val fotoEvidencia = resultados["foto_evidencia_gestion"]?.toString()
                if (!fotoEvidencia.isNullOrEmpty()) {
                    respaldoDAO.insertar(operacionId, "foto_evidencia_gestion", fotoEvidencia)
                    Log.d("FormularioActivity", "Foto respaldada en SQLite: $fotoEvidencia")
                }

                Toast.makeText(this, "Evidencias guardadas localmente", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("FormularioActivity", "Error al guardar respaldo local: ${e.message}")
            }


            val gestionRepo = GestionRepository(this)
            gestionRepo.postGestion(
                gestion,
                onSuccess = { gestionCreada ->
                    Toast.makeText(this, "Gestión enviada correctamente", Toast.LENGTH_SHORT).show()

                    val gestionJson = Gson().toJson(gestionCreada)
                    val resultIntent = Intent().apply {
                        putExtra("GESTION_JSON", gestionJson)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                },
                onError = { error ->
                    Toast.makeText(
                        this,
                        "Error al enviar la gestión: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun agregarCamposFijos() {
        val labelRespuesta = crearLabel("Respuesta de la gestión")
        containerFormulario.addView(labelRespuesta)

        val textInputLayoutRespuesta = com.google.android.material.textfield.TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }

            hint = "Respuesta de la gestión"
            isHintEnabled = true
            setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
            setBoxStrokeColor(ContextCompat.getColor(context, R.color.custom_primary))
            setBoxCornerRadii(16f, 16f, 16f, 16f)
        }

        val catalogo = Catalogo.fetchCatalogos().find { it.nombreCatalogo == "RespuestasGestion" }
        val detalles = catalogo?.detallesCatalogo ?: emptyList()
        val items = detalles.map { it.descripcion }

        val spinnerRespuesta = Spinner(this, Spinner.MODE_DROPDOWN).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = ContextCompat.getDrawable(context, R.drawable.bg_spinner_outlined)
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
        }

        textInputLayoutRespuesta.addView(spinnerRespuesta)
        containerFormulario.addView(textInputLayoutRespuesta)
        respuestas["respuesta_gestion"] = detalles.firstOrNull()?.codigoDetalle ?: ""

        spinnerRespuesta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                respuestas["respuesta_gestion"] = detalles[pos].codigoDetalle
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                respuestas["respuesta_gestion"] = null
            }
        }

        val labelFoto = crearLabel("Evidencia de gestión")
        containerFormulario.addView(labelFoto)

        val buttonFoto = Button(this).apply {
            text = "Subir evidencia"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }
        containerFormulario.addView(buttonFoto)

        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            ).apply { bottomMargin = 32 }
            scaleType = ImageView.ScaleType.CENTER_CROP
            visibility = View.GONE
            tag = "foto_evidencia_gestion"
            setPadding(8, 8, 8, 8)
        }
        containerFormulario.addView(imageView)

        respuestas["foto_evidencia_gestion"] = null

        buttonFoto.setOnClickListener {
            currentPhotoCampo = "foto_evidencia_gestion"
            val (uri, path) = createImageFile("foto_evidencia_gestion")
            currentPhotoUri = uri
            currentPhotoPath = path
            takePictureLauncher.launch(uri)
        }

        val labelObservacion = crearLabel("Observación de la gestión")
        containerFormulario.addView(labelObservacion)

        val textInputLayoutObs = com.google.android.material.textfield.TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }

            hint = "Observación"
            isHintEnabled = true
            setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
            setBoxStrokeColor(ContextCompat.getColor(context, R.color.custom_primary))
            setBoxCornerRadii(16f, 16f, 16f, 16f)
        }

        val editTextObs = com.google.android.material.textfield.TextInputEditText(textInputLayoutObs.context).apply {
            minLines = 3
            maxLines = 6
            setPadding(24, 24, 24, 24)
            background = null
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }

        textInputLayoutObs.addView(editTextObs)
        containerFormulario.addView(textInputLayoutObs)
        respuestas["observacion_gestion"] = editTextObs
    }

    private fun crearCamposDinamicos(campos: List<CampoFormulario>) {
        for (campo in campos) {
            val label = crearLabel(campo.etiqueta)
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
                        setBoxStrokeColor(ContextCompat.getColor(context, R.color.custom_primary))
                    }

                    val editText = com.google.android.material.textfield.TextInputEditText(textInputLayout.context)
                    textInputLayout.addView(editText)
                    containerFormulario.addView(textInputLayout)
                    respuestas[campo.nombreCampo] = editText
                }

                TipoCampo.SELECT -> {
                    val catalogo = Catalogo.fetchCatalogos().find { it.nombreCatalogo == campo.nombreCatalogo }
                    val detalles = catalogo?.detallesCatalogo ?: emptyList()
                    val items = detalles.map { it.descripcion }

                    val textInputLayout = com.google.android.material.textfield.TextInputLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 32 }
                        hint = campo.etiqueta
                        setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
                        setBoxStrokeColor(ContextCompat.getColor(context, R.color.custom_primary))
                    }

                    val spinner = Spinner(this, Spinner.MODE_DROPDOWN).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        background = ContextCompat.getDrawable(context, R.drawable.bg_spinner_outlined)
                        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
                    }

                    textInputLayout.addView(spinner)
                    containerFormulario.addView(textInputLayout)
                    respuestas[campo.nombreCampo] = detalles.firstOrNull()?.codigoDetalle ?: ""

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                            respuestas[campo.nombreCampo] = detalles[pos].codigoDetalle
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            respuestas[campo.nombreCampo] = null
                        }
                    }
                }

                TipoCampo.FOTO -> {
                    val btn = Button(this).apply {
                        text = "Subir ${campo.etiqueta}"
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 16 }
                    }
                    containerFormulario.addView(btn)

                    val imageView = ImageView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            600
                        ).apply { bottomMargin = 32 }
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        visibility = View.GONE
                        tag = campo.nombreCampo
                    }
                    containerFormulario.addView(imageView)

                    respuestas[campo.nombreCampo] = null

                    btn.setOnClickListener {
                        currentPhotoCampo = campo.nombreCampo
                        val (uri, path) = createImageFile(campo.nombreCampo)
                        currentPhotoUri = uri
                        currentPhotoPath = path
                        takePictureLauncher.launch(uri)
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
                        isFocusable = false
                        isClickable = true
                        setOnClickListener {
                            val calendar = java.util.Calendar.getInstance()
                            val datePicker = android.app.DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    setText(String.format("%02d/%02d/%04d", day, month + 1, year))
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

    private fun crearLabel(texto: String) = TextView(this).apply {
        text = texto
        textSize = 16f
        setTypeface(typeface, Typeface.BOLD)
        setPadding(0, 24, 0, 8)
    }

    private fun createImageFile(nombreCampo: String): Pair<Uri, String> {
        val file = File.createTempFile("IMG_${nombreCampo}_", ".jpg", cacheDir)
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        return uri to file.absolutePath
    }

    private fun crearGestionDesdeFormulario(
        idOperacion: Int,
        respuestasFormulario: Map<String, Any?>,
        audioPath: String?
    ): Gestion {
        val formularioJson = Gson().toJson(respuestasFormulario)
        return Gestion(
            idGestion = (0..999999).random(),
            idOperacion = idOperacion,
            fechaRegistro = LocalDateTime.now().toString(),
            respuesta = respuestasFormulario["respuesta_gestion"]?.toString() ?: "",
            formularioJson = formularioJson,
            urlGrabacionVoz = audioPath,
            urlFotoEvidencia = respuestasFormulario["foto_evidencia_gestion"]?.toString(),
            observacion = respuestasFormulario["observacion_gestion"]?.toString() ?: "",
            operacionNavigation = null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::audioRecorder.isInitialized) audioRecorder.finalizarGrabacion()
    }
}
