package com.ehts.ehtswatch

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer


class AudioRecordActivity : Activity() {

    //Define AudioRecord Object and other parameters
    private val RECORDER_SAMPLE_RATE = 44100
    private val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
    private val RAW_AUDIO_SOURCE = MediaRecorder.AudioSource.UNPROCESSED
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(
        RECORDER_SAMPLE_RATE,
        CHANNEL_CONFIG,
        AUDIO_FORMAT
    )

    private lateinit var audioRecord: AudioRecord

    private val recordingRunnable = Runnable {
        val file = File(applicationContext.filesDir, "recording.pcm")
        val buffer = ByteBuffer.allocateDirect(BUFFER_SIZE_RECORDING)
        val startTime = System.currentTimeMillis()
        audioRecord.startRecording()
        Log.d("asdasd", "Starting audio recording")
        try {
            FileOutputStream(file).use { outStream ->
                while (System.currentTimeMillis() - startTime < 10000) {
                    val result: Int = audioRecord.read(buffer, BUFFER_SIZE_RECORDING)
                    if (result < 0) {
                        throw RuntimeException("Reading of audio buffer failed: " + result)
                    }
                    outStream.write(buffer.array(), 0, BUFFER_SIZE_RECORDING)
                    buffer.clear()
                }
                audioRecord.stop()
                audioRecord.release()
                Log.d("asdasd", "Stopped audio recording")
                finish()
            }
        } catch (e: IOException) {
            throw RuntimeException("Writing of recorded audio failed", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_record)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Missing Permission", Toast.LENGTH_SHORT).show()
            return
        }

        audioRecord = AudioRecord(
            AUDIO_SOURCE,
            RECORDER_SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE_RECORDING
        )

        Thread(recordingRunnable).start()
    }
}