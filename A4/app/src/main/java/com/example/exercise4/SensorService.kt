package com.example.exercise4

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.*
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

class SensorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lastAcceleration = 0f
    private var acceleration = 0f
    private var lastUpdate: Long = 0
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.let {
            val x = it[0]
            val y = it[1]
            val z = it[2]

            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastUpdate) > 100) {
                lastUpdate = currentTime

                val currentAcceleration = sqrt(x * x + y * y + z * z)
                val deltaAcceleration = currentAcceleration - lastAcceleration
                acceleration = acceleration * 0.9f + deltaAcceleration // Low-pass filter

                if (acceleration > 12) {
                    _shakeState.value = true
                    playSound()
                    sendNotification()
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000)
                        _shakeState.value = false
                    }
                }

                lastAcceleration = currentAcceleration
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        mediaPlayer?.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun sendNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "SHAKE_SERVICE_CHANNEL")
            .setSmallIcon(android.R.drawable.btn_star)
            .setContentTitle("Shake Detected!")
            .setContentText("You woke up the cat! \uD83D\uDC3E ")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(2, notification)
    }

    private fun playSound() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.meow)
        mediaPlayer?.start()
    }


    companion object {
        private val _shakeState = MutableStateFlow(false)
        val shakeState = _shakeState.asStateFlow()

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "SHAKE_SERVICE_CHANNEL",
                    "Shake Detection Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                )

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
