package com.example.assign3.wear.watch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WearApp(
                    name = "Qianyu CAO",
                    onOpenSensors = {
                        startActivity(Intent(this,SensorActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun WearApp(
    name: String,
    onOpenSensors: () -> Unit,
) {
    MaterialTheme {
        WelcomeScreen(
            name = name,
            onOpenSensors = onOpenSensors,
        )
    }
}

@Composable
private fun WelcomeScreen(
    name: String,
    onOpenSensors: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenSensors,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "A3-Open Sensors",
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(
        name = "Qianyu CAO",
        onOpenSensors = {},
    )
}
