package com.upc.appgestiones.core.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException

class AudioRecorderService(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var filePath: String? = null
    private var isRecording: Boolean = false

    // Verifica si se tiene permiso de micrófono
    fun verificarAccesoAlMicro(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Inicia la grabación
    fun iniciarGrabacion(nombreArchivo: String = "grabacion_${System.currentTimeMillis()}.mp3") {
        if (isRecording) return

        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val outputFile = File(outputDir, nombreArchivo)
        filePath = outputFile.absolutePath

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(filePath)

            try {
                prepare()
                start()
                isRecording = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Pausar grabación (solo disponible a partir de API 24)
    fun pausarGrabacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.pause()
        }
    }

    // Reanudar grabación
    fun reanudarGrabacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.resume()
        }
    }

    // Finalizar y guardar grabación
    fun finalizarGrabacion() {
        if (isRecording) {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
            mediaRecorder = null
            isRecording = false
        }
    }

    // Obtener la ruta del archivo grabado
    fun obtenerPathGrabacion(): String? {
        return filePath
    }
}
