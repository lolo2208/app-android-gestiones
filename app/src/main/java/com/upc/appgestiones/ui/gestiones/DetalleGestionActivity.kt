package com.upc.appgestiones.ui.gestiones

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Gestion

class DetalleGestionActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_realizada)

        val gestion = intent.getSerializableExtra("gestion") as? Gestion

        val txtIdGestion = findViewById<TextView>(R.id.txtIdGestion)
        val txtIdOperacion = findViewById<TextView>(R.id.txtIdOperacion)
        val txtFechaRegistro = findViewById<TextView>(R.id.txtFechaRegistro)
        val txtEstadoOperacion = findViewById<TextView>(R.id.txtEstadoOperacion)
        val txtFormulario = findViewById<TextView>(R.id.txtFormulario)
        val imgEvidencia = findViewById<ImageView>(R.id.imgEvidencia)
        val btnPlayAudio = findViewById<ImageView>(R.id.btnPlayAudio)

        gestion?.let {
            txtIdGestion.text = "ID Gestión: ${it.idGestion}"
            txtIdOperacion.text = "ID Operación: ${it.idOperacion}"
            txtFechaRegistro.text = "Fecha: ${it.fechaRegistro}"
            txtEstadoOperacion.text = "Estado: ${it.operacionNavigation!!.estado}"
            txtFormulario.text = "Formulario: ${it.formularioJson}"

            if (!it.urlFotoEvidencia.isNullOrEmpty()) {
                Glide.with(this).load(it.urlFotoEvidencia).into(imgEvidencia)
            }

            if (!it.urlGrabacionVoz.isNullOrEmpty()) {
                btnPlayAudio.visibility = ImageView.VISIBLE
                btnPlayAudio.setOnClickListener { _ ->
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(it.urlGrabacionVoz)
                        prepare()
                        start()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
