package com.example.assign3.phone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "A3 - Phone App",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        item {
            Text(
                text = "Qianyu CAO",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        item {
            PhoneDataSection(
                title = "Accelerometer",
                value = uiState.accelerometerText,
            )
        }
        item {
            PhoneDataSection(
                title = "Gyroscope",
                value = uiState.gyroscopeText,
            )
        }
        item {
            PhoneDataSection(
                title = "Heart Rate",
                value = uiState.heartRateText,
            )
        }
    }
}

@Composable
private fun PhoneDataSection(
    title: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFEAF7EA),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            textAlign = TextAlign.Center,
            color = Color.Black,
        )
    }
}

@Preview
@Composable
private fun PhoneScreenPreview() {
    MaterialTheme {
        PhoneScreen(
            uiState = PhoneUiState(
                accelerometerText = "Accelerometer  X: 0.12  Y: 9.81  Z: -0.34",
                gyroscopeText = "Gyroscope  X: 0.01  Y: -0.02  Z: 0.03",
                heartRateText = "Heart Rate  BPM: 72",
            )
        )
    }
}
