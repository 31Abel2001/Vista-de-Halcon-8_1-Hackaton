package com.example.vista_halcon_hackaton

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: String? = null

    fun startRecording(): String? {
        return try {
            val fileName = "audio_${System.currentTimeMillis()}.mp3"
            val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            if (fileDir == null) {
                println("No se pudo acceder al directorio de música.")
                return null
            }
            val file = File(fileDir, fileName)
            outputFile = file.absolutePath

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile)
                prepare()
                start()
            }

            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
        }
    }
}