package com.ehts.ehtswatch

// HeartRateForegroundService.kt
import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.core.content.ContextCompat

class HeartRateForegroundService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private var heartRateData: MutableList<Double> = ArrayList()

    // Implement the necessary methods for SensorEventListener
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val heartRateValue = event.values[0].toDouble()
            // Add the heart rate value to the heartRateData list
            heartRateData.add(heartRateValue)
            // You can also push the heart rate value to your database here if needed
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_ACTION) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
            stopSelf()
        } else {
            val notificationString = intent?.getStringExtra("notification")
            val notification = createNotification(notificationString)
            startForeground(NOTIFICATION_ID, notification)

            // Initialize sensor manager
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

            // Check if body sensors permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
                // Register the heart rate sensor listener
                registerHeartRateSensorListener()
            } else {
                // Request the body sensors permission
                requestBodySensorsPermission()
            }
        }
        return START_STICKY
    }

    private fun registerHeartRateSensorListener() {
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun unregisterHeartRateSensorListener() {
        sensorManager.unregisterListener(this, heartRateSensor)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterHeartRateSensorListener()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(notificationString: String?): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "EHTS Test",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.loginlogo)
            .setContentText(notificationString)
            .build()
    }

    private fun requestBodySensorsPermission() {
        // Request body sensors permission here
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "HeartRateChannel"
        private const val NOTIFICATION_ID = 1
        const val STOP_ACTION = "StopRecordingAction"
        const val NOTIFICATION_EXTRA = "notification"
    }
}
