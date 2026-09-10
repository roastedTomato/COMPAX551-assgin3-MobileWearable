package com.example.assign3.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {
    private val uiState = mutableStateOf(PhoneUiState())
    private val sensorProcessor = SensorProcessor()
    private val accelerometerReadings = mutableListOf<MotionReading>()
    private val gyroscopeReadings = mutableListOf<MotionReading>()
    private val heartRateReadings = mutableListOf<HeartRateReading>()
    private var latestAccelerometerReading: MotionReading? = null
    private var latestGyroscopeReading: MotionReading? = null
    private var latestHeartRateReading: HeartRateReading? = null
    private lateinit var wearDataReceiver: WearDataReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wearDataReceiver = WearDataReceiver(
            context = this,
            onAccelerometerReceived = { reading ->
                runOnUiThread {
                    latestAccelerometerReading = reading
                    accelerometerReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        accelerometer = sensorProcessor.processAccelerometer(accelerometerReadings),
                        isReceivingWatchData = true,
                    )
                }
            },
            onGyroscopeReceived = { reading ->
                runOnUiThread {
                    latestGyroscopeReading = reading
                    gyroscopeReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        gyroscope = sensorProcessor.processGyroscope(gyroscopeReadings),
                        isReceivingWatchData = true,
                    )
                }
            },
            onHeartRateReceived = { reading ->
                runOnUiThread {
                    latestHeartRateReading = reading
                    heartRateReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        heartRate = sensorProcessor.processHeartRate(heartRateReadings),
                        isReceivingWatchData = true,
                    )
                }
            }
        )

        setContent {
            MaterialTheme {
                PhoneScreen(
                    uiState = uiState.value,
                    onResetSession = ::resetSession,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        wearDataReceiver.start()
    }

    override fun onPause() {
        wearDataReceiver.stop()
        super.onPause()
    }

    private fun resetSession() {
        accelerometerReadings.clear()
        gyroscopeReadings.clear()
        heartRateReadings.clear()

        latestAccelerometerReading?.let { accelerometerReadings.add(it) }
        latestGyroscopeReading?.let { gyroscopeReadings.add(it) }
        latestHeartRateReading?.let { heartRateReadings.add(it) }

        uiState.value = PhoneUiState(
            accelerometer = sensorProcessor.processAccelerometer(accelerometerReadings),
            gyroscope = sensorProcessor.processGyroscope(gyroscopeReadings),
            heartRate = sensorProcessor.processHeartRate(heartRateReadings),
            isReceivingWatchData = latestAccelerometerReading != null ||
                latestGyroscopeReading != null ||
                latestHeartRateReading != null,
        )
    }
}

private fun <T> MutableList<T>.addReading(reading: T) {
    add(reading)
    if (size > MAX_READING_HISTORY) {
        removeAt(0)
    }
}

private const val MAX_READING_HISTORY = 120
