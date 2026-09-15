package com.example.assign3.phone

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val uiState = mutableStateOf(PhoneUiState())
    private val sensorProcessor = SensorProcessor()
    private val accelerometerReadings = mutableListOf<MotionReading>()
    private val gyroscopeReadings = mutableListOf<MotionReading>()
    private val heartRateReadings = mutableListOf<HeartRateReading>()
    private var latestAccelerometerReading: MotionReading? = null
    private var latestGyroscopeReading: MotionReading? = null
    private var latestHeartRateReading: HeartRateReading? = null
    private var lastWatchDataReceivedAt = 0L
    private lateinit var wearDataReceiver: WearDataReceiver
    private val staleWatchDataCheck = object : Runnable {
        override fun run() {
            val elapsed = System.currentTimeMillis() - lastWatchDataReceivedAt
            if (lastWatchDataReceivedAt > 0L && elapsed >= WATCH_DATA_STALE_TIMEOUT_MS) {
                uiState.value = uiState.value.copy(connectionState = WatchConnectionState.Stale)
            } else {
                scheduleStaleWatchDataCheck()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wearDataReceiver = WearDataReceiver(
            context = this,
            onAccelerometerReceived = { reading ->
                runOnUiThread {
                    if (!reading.isNewerThan(latestAccelerometerReading)) {
                        return@runOnUiThread
                    }
                    markWatchDataReceived()
                    latestAccelerometerReading = reading
                    accelerometerReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        accelerometer = sensorProcessor.processAccelerometer(accelerometerReadings),
                        connectionState = WatchConnectionState.Receiving,
                    )
                }
            },
            onGyroscopeReceived = { reading ->
                runOnUiThread {
                    if (!reading.isNewerThan(latestGyroscopeReading)) {
                        return@runOnUiThread
                    }
                    markWatchDataReceived()
                    latestGyroscopeReading = reading
                    gyroscopeReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        gyroscope = sensorProcessor.processGyroscope(gyroscopeReadings),
                        connectionState = WatchConnectionState.Receiving,
                    )
                }
            },
            onHeartRateReceived = { reading ->
                runOnUiThread {
                    if (!reading.isNewerThan(latestHeartRateReading)) {
                        return@runOnUiThread
                    }
                    markWatchDataReceived()
                    latestHeartRateReading = reading
                    heartRateReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        heartRate = sensorProcessor.processHeartRate(heartRateReadings),
                        connectionState = WatchConnectionState.Receiving,
                    )
                }
            }
        )

        setContent {
            MaterialTheme {
                PhoneScreen(
                    uiState = uiState.value,
                    onResetSession = ::resetPhoneSession,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        wearDataReceiver.start()
        scheduleStaleWatchDataCheck()
    }

    override fun onPause() {
        mainHandler.removeCallbacks(staleWatchDataCheck)
        wearDataReceiver.stop()
        super.onPause()
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks(staleWatchDataCheck)
        wearDataReceiver.stop()
        super.onDestroy()
    }

    private fun resetPhoneSession() {
        accelerometerReadings.clear()
        gyroscopeReadings.clear()
        heartRateReadings.clear()

        // Reset clears the phone-side session history while keeping the latest live samples as the new baseline.
        latestAccelerometerReading?.let { accelerometerReadings.add(it) }
        latestGyroscopeReading?.let { gyroscopeReadings.add(it) }
        latestHeartRateReading?.let { heartRateReadings.add(it) }

        uiState.value = PhoneUiState(
            accelerometer = sensorProcessor.processAccelerometer(accelerometerReadings),
            gyroscope = sensorProcessor.processGyroscope(gyroscopeReadings),
            heartRate = sensorProcessor.processHeartRate(heartRateReadings),
            connectionState = currentConnectionState(),
        )
    }

    private fun markWatchDataReceived() {
        lastWatchDataReceivedAt = System.currentTimeMillis()
        scheduleStaleWatchDataCheck()
    }

    private fun scheduleStaleWatchDataCheck() {
        mainHandler.removeCallbacks(staleWatchDataCheck)//Prevent the accumulation of multiple detection tasks. Whenever new data is received, the existing countdown is cancelled and the timer restarts.
        mainHandler.postDelayed(staleWatchDataCheck, WATCH_DATA_STALE_TIMEOUT_MS)
    }

    private fun currentConnectionState(): WatchConnectionState {
        val hasReceivedData = latestAccelerometerReading != null ||
            latestGyroscopeReading != null ||
            latestHeartRateReading != null
        if (!hasReceivedData) {
            return WatchConnectionState.Waiting
        }

        val elapsed = System.currentTimeMillis() - lastWatchDataReceivedAt
        return if (elapsed >= WATCH_DATA_STALE_TIMEOUT_MS) {
            WatchConnectionState.Stale
        } else {
            WatchConnectionState.Receiving
        }
    }
}

private fun MotionReading.isNewerThan(previous: MotionReading?): Boolean {
    return previous == null || timestamp >= previous.timestamp
}

private fun HeartRateReading.isNewerThan(previous: HeartRateReading?): Boolean {
    return previous == null || timestamp >= previous.timestamp
}

private fun <T> MutableList<T>.addReading(reading: T) {
    add(reading)
    if (size > MAX_READING_HISTORY) {
        removeAt(0)
    }
}

private const val MAX_READING_HISTORY = 120
private const val WATCH_DATA_STALE_TIMEOUT_MS = 5_000L
