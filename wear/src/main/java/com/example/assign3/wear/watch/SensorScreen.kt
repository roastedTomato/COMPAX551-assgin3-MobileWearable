package com.example.assign3.wear.watch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices
import java.util.Locale

@Composable
fun SensorApp(
    accelerometerX: Float = 0f,
    accelerometerY: Float = 0f,
    accelerometerZ: Float = 0f,
    gyroscopeX: Float = 0f,
    gyroscopeY: Float = 0f,
    gyroscopeZ: Float = 0f,
    isAccelerometerAvailable: Boolean = true,
    isGyroscopeAvailable: Boolean = true,
    heartRateBpm: Float = 0f,
    isHeartRateAvailable: Boolean = false,
) {
    MaterialTheme {
        ScreenScaffold { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            ) {
                item {
                    SensorSection(
                        title = "Accelerometer",
                        isAvailable = isAccelerometerAvailable,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ){
                            SensorValue(label = "X", value = accelerometerX)
                            SensorValue(label = "Y", value = accelerometerY)
                            SensorValue(label = "Z", value = accelerometerZ)
                        }
                    }
                }

                item {
                    SensorSection(
                        title = "Gyroscope",
                        isAvailable = isGyroscopeAvailable,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {  SensorValue(label = "X", value = gyroscopeX)
                            SensorValue(label = "Y", value = gyroscopeY)
                            SensorValue(label = "Z", value = gyroscopeZ)
                        }
                    }
                }

                item {
                    SensorSection(
                        title = "Heart Rate",
                        isAvailable = isHeartRateAvailable,
                    ) {
                        SensorValue(label = "BPM", value = heartRateBpm)
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorSection(
    title: String,
    isAvailable: Boolean,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFEAF7EA),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isAvailable) {
            content()
        } else {
            Text(
                text = "unavailable",
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
        }
    }
}

@Composable
private fun SensorValue(
    label: String,
    value: Float,
) {
    val formattedValue = String.format(Locale.US, "%.2f", value)
    Text(
        text = "$label:$formattedValue",
        fontSize = 10.sp,
        color = Color.Black
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun SensorPreview() {
    SensorApp(
        accelerometerX = 0.12f,
        accelerometerY = 9.81f,
        accelerometerZ = -0.34f,
        gyroscopeX = 0.01f,
        gyroscopeY = -0.02f,
        gyroscopeZ = 0.03f,
        heartRateBpm = 72f,
        isHeartRateAvailable = true,
    )
}
