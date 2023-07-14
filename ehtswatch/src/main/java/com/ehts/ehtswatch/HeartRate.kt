package com.ehts.ehtswatch

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

//This class records a users heart rate continuously and evaluates the user low, resting, and max hr and records the timestamp
//User can administer multiple tests
//this feature runs in the background even when wifi isn't connected without the user knowing whats being recorded
//after test is over stop action sends the test results to the database
class HeartRate : Activity(), SensorEventListener {
    private companion object {
        private const val TAG = "HeartRateMonitor"
        private const val PERMISSION_REQUEST_BODY_SENSORS = 1
        private const val NOTIFICATION_CHANNEL_ID = "HeartRateChannel"
        private const val NOTIFICATION_ID = 1
        private const val SERVICE_ID = 1
        private const val STOP_ACTION = "StopRecordingAction"
        private const val TIMER_INTERVAL = 1 * 60 * 1000 // 5 minutes in milliseconds
    }

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private var heartRateData: MutableList<Double> = ArrayList()
    private var recordingStartTime: Long = 0L
    private var recordingStopTime: Long = 0L
    private var restingHeartRate = 0.0
    private var maxHeartRate = 0.0
    private var lowHeartRate = 0.0
    private lateinit var goBackButton: Button
    private lateinit var stopButton: Button
    private lateinit var startButton: Button
    private lateinit var textHeartRate: TextView
    private lateinit var databaseReference: DatabaseReference
    private var empId: String? = null
    private var timerHandler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)

        val bundle = intent.extras
        if (bundle != null) {
            empId = bundle.getString("Employee ID")
        }
        // Initialize sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        // Check if body sensors permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), PERMISSION_REQUEST_BODY_SENSORS)
        } else {
            registerHeartRateSensorListener()
        }

        // Find the TextView and buttons in the layout
        textHeartRate = findViewById(R.id.textHeartRate)
        goBackButton = findViewById(R.id.gobackbtn)
        stopButton = findViewById(R.id.stopButton)
        startButton = findViewById(R.id.startButton)

        // Set click listener for the "Go Back" button
        goBackButton.setOnClickListener {
            // Navigate back to MainActivity
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set click listener for the "Stop" button
        stopButton.setOnClickListener {
            unregisterHeartRateSensorListener()

            stopHeartRateRecordingService()
         //   calculateHeartRateMetrics()
            stopButton.visibility = Button.INVISIBLE
            Toast.makeText(this, "Test Ended", Toast.LENGTH_SHORT).show() // Display a toast message
        }

        // Set click listener for the "Start" button
        startButton.setOnClickListener {
            recordingStartTime = System.currentTimeMillis() // Get the start timestamp when the button is clicked
            startHeartRateRecording()
            startButton.visibility = Button.INVISIBLE // Hide the start button
            Toast.makeText(this, "Test started", Toast.LENGTH_SHORT).show() // Display a toast message

        }
    }

    private fun registerHeartRateSensorListener() {
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    private fun unregisterHeartRateSensorListener() {
        sensorManager.unregisterListener(this, heartRateSensor)
    }

    override fun onResume() {
        super.onResume()
        registerHeartRateSensorListener()
    }

    override fun onPause() {
        super.onPause()
        unregisterHeartRateSensorListener()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_BODY_SENSORS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerHeartRateSensorListener()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val heartRateValue = event.values[0].toDouble()
            heartRateData.add(heartRateValue)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    private fun calculateHeartRateMetrics() {
        if (heartRateData.isNotEmpty()) {
            var sum = 0.0
            var min = heartRateData[0]
            var max = heartRateData[0]
            for (heartRate in heartRateData) {
                sum += heartRate
                if (heartRate < min) {
                    min = heartRate
                }
                if (heartRate > max) {
                    max = heartRate
                }
            }
            restingHeartRate = sum / heartRateData.size
            lowHeartRate = min
            maxHeartRate = max

            // Get the timestamp of the recording

            val recordingStartTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                .format(Date(recordingStartTime))
            val recordingStopTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                .format(Date())



            // Store the heart rate data in your database

            val data = SensorsData(restingHeartRate, lowHeartRate, maxHeartRate, recordingStartTimestamp, recordingStopTimestamp)


            // Print the heart rate metrics and recording timestamp
            val heartRateText = String.format(
                Locale.getDefault(),
                "Heart Rate\nResting: %.1f\nLow: %.1f\nMax: %.1f\nRecording Start Timestamp: %s\nRecording Stop Timestamp: %s",
                restingHeartRate, lowHeartRate, maxHeartRate, recordingStartTimestamp, recordingStopTimestamp
            )
            textHeartRate.text = heartRateText

            // Get the user ID
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val myRef = FirebaseDatabase.getInstance().reference.child("users").child(userId?:"")
                .child("employees").child(empId?:"")
                .child("sensors_record").child(empId?:"")
            val uniqueKey: String? = myRef.push().key
            userId?.let {
                FirebaseDatabase.getInstance().reference.child("users").child(it)
                    .child("employees").child(empId?:"")
                    .child("sensors_record").child(empId?:"").child(uniqueKey ?: Random().toString()).setValue(data)
                    .addOnSuccessListener {
                        Toast.makeText(this@HeartRate, "Saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@HeartRate, "Failed to save data", Toast.LENGTH_SHORT).show()
                    }

            }

        }
    }

    private fun startHeartRateRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Heart Rate",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.loginlogo)
            .build()

        val serviceIntent = Intent(this, HeartRateRecordingService::class.java)
        serviceIntent.putExtra(HeartRateRecordingService.NOTIFICATION_EXTRA, notification)
        ContextCompat.startForegroundService(this, serviceIntent)

        recordingStartTime = System.currentTimeMillis()

        // Delay the calculation of heart rate metrics
        timerHandler.postDelayed({
            calculateHeartRateMetrics()
            startHeartRateRecording()
        }, TIMER_INTERVAL.toLong())

        startTimer()
    }

    private fun stopHeartRateRecordingService() {
        val serviceIntent = Intent(this, HeartRateRecordingService::class.java)
        serviceIntent.action = STOP_ACTION
        stopService(serviceIntent)
        recordingStopTime = System.currentTimeMillis()
        timerHandler.removeCallbacks(timerRunnable!!)
    }
    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                if (heartRateData.size > 0) {
                  //  calculateHeartRateMetrics()
                }
            }
        }
        timerHandler.postDelayed(timerRunnable!!, TIMER_INTERVAL.toLong())
    }

    /**
     * Foreground service for heart rate recording
     */
    class HeartRateRecordingService : Service() {
        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            if (intent?.action == STOP_ACTION) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
                stopSelf()
            } else {
                val notificationString = intent?.getStringExtra("notification")
                val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.loginlogo)
                    .setContentText(notificationString)
                    .build()
                startForeground(NOTIFICATION_ID, notification)
            }
            return START_STICKY
        }

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

        companion object {
            const val NOTIFICATION_EXTRA = "notification"
        }
    }
}
