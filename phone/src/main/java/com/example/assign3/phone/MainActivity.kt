package com.example.assign3.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {
    private val uiState = mutableStateOf(PhoneUiState())
    private lateinit var wearDataReceiver: WearDataReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wearDataReceiver = WearDataReceiver(
            context = this,
            onAccelerometerReceived = { accelerometerText ->
                runOnUiThread {
                    uiState.value = uiState.value.copy(accelerometerText = accelerometerText)
                }
            },
            onGyroscopeReceived = { gyroscopeText ->
                runOnUiThread {
                    uiState.value = uiState.value.copy(gyroscopeText = gyroscopeText)
                }
            },
            onHeartRateReceived = { heartRateText ->
                runOnUiThread {
                    uiState.value = uiState.value.copy(heartRateText = heartRateText)
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
