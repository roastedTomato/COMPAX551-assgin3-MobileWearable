package com.example.assign3.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.util.Locale

class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {
    private var accelerometerText = androidx.compose.runtime.mutableStateOf("Waiting for watch data")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PhoneApp(accelerometerText = accelerometerText.value)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        Wearable.getMessageClient(this).removeListener(this)
        super.onPause()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path != ACCELEROMETER_MESSAGE_PATH) {
            return
        }

        val message = messageEvent.data.toString(Charsets.UTF_8)
        val parts = message.split(",")
        if (parts.size != 4) {
            return
        }

        val x = parts[1].toFloatOrNull() ?: return
        val y = parts[2].toFloatOrNull() ?: return
        val z = parts[3].toFloatOrNull() ?: return

        runOnUiThread {
            accelerometerText.value = String.format(
                Locale.US,
                "Accelerometer  X: %.2f  Y: %.2f  Z: %.2f",
                x,
                y,
                z,
            )
        }
    }
}

@Composable
private fun PhoneApp(accelerometerText: String) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FB)),
        color = Color(0xFFF6F8FB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "A3 - Phone App",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Qianyu CAO",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = accelerometerText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PhoneAppPreview() {
    MaterialTheme {
        PhoneApp(accelerometerText = "Accelerometer  X: 0.12  Y: 9.81  Z: -0.34")
    }
}

private const val ACCELEROMETER_MESSAGE_PATH = "/motion/accelerometer"
