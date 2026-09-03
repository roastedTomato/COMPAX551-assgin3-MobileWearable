package com.example.assign3.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WearApp()
            }
        }
    }
}

@Composable
private fun WearApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "A3 Exertion Watch",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Ready for Part B sensor capture",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFC7D3E0),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = {},
            modifier = Modifier.padding(top = 16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Start")
        }
        Text(
            text = "Phone pairing: ready",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF8DDACF),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}

@Preview(
    name = "Wear home",
    device = "id:wearos_large_round",
    showSystemUi = true
)
@Composable
private fun WearAppPreview() {
    MaterialTheme {
        WearApp()
    }
}
