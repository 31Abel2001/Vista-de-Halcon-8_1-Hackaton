package com.utc.vistadehalcon

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: String? = null

    @SuppressLint("MissingPermission")
    fun startRecording(): String? {
        return try {
            // 📁 Carpeta pública: /Music/Notas de voz
            val voiceNotesDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                "Notas de voz"
            )
            if (!voiceNotesDir.exists()) {
                voiceNotesDir.mkdirs()
            }

            // 📄 Nombre del archivo con fecha
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Grabacion_$timeStamp.mp3"
            outputFile = File(voiceNotesDir, fileName).absolutePath

            // 🎙️ Configuración del grabador
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile)
                prepare()
                start()
            }

            Toast.makeText(context, "Grabando audio...", Toast.LENGTH_SHORT).show()
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al grabar: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null

            outputFile?.let {
                Toast.makeText(
                    context,
                    "Grabación guardada en: $it",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al detener grabación", Toast.LENGTH_SHORT).show()
        }
    }
}
