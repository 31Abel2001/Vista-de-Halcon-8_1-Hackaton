package com.utc.vistadehalcon

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import java.io.File

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: String? = null

    fun startRecording(): String? {
        try {
            // Carpeta donde se guardará el archivo
            val voiceNotesDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                "Notas de voz"
            )

            if (!voiceNotesDir.exists()) {
                voiceNotesDir.mkdirs()
            }

            // Nombre del archivo
            val fileName = "Grabacion_${System.currentTimeMillis()}.mp3"
            outputFile = File(voiceNotesDir, fileName).absolutePath

            // Configurar MediaRecorder
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile)
                prepare()
                start()
            }

            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
