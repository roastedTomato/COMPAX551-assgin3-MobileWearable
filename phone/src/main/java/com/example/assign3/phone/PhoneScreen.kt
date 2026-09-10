package com.example.assign3.phone

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

@Composable
fun PhoneScreen(uiState: PhoneUiState) {
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
                text = uiState.accelerometerText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PhoneScreenPreview() {
    MaterialTheme {
        PhoneScreen(
            uiState = PhoneUiState(
                accelerometerText = "Accelerometer  X: 0.12  Y: 9.81  Z: -0.34",
            )
        )
    }
}
