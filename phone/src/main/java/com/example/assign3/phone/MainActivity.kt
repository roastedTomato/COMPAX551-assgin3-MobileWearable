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
    private lateinit var wearDataReceiver: WearDataReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wearDataReceiver = WearDataReceiver(
            context = this,
            onAccelerometerReceived = { reading ->
                runOnUiThread {
                    accelerometerReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        accelerometer = sensorProcessor.processAccelerometer(accelerometerReadings)
                    )
                }
            },
            onGyroscopeReceived = { reading ->
                runOnUiThread {
                    gyroscopeReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        gyroscope = sensorProcessor.processGyroscope(gyroscopeReadings)
                    )
                }
            },
            onHeartRateReceived = { reading ->
                runOnUiThread {
                    heartRateReadings.addReading(reading)
                    uiState.value = uiState.value.copy(
                        heartRate = sensorProcessor.processHeartRate(heartRateReadings)
                    )
                }
            }
        )

        setContent {
            MaterialTheme {
                PhoneScreen(uiState = uiState.value)
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
}

private fun <T> MutableList<T>.addReading(reading: T) {
    add(reading)
    if (size > MAX_READING_HISTORY) {
        removeAt(0)
    }
}

private const val MAX_READING_HISTORY = 120
