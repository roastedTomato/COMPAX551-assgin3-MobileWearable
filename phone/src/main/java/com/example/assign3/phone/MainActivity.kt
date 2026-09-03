package com.example.assign3.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PhoneApp()
            }
        }
    }
}

@Composable
private fun PhoneApp() {
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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "A3 Exertion Phone",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16202A)
            )
            Text(
                text = "Companion dashboard for live Wear OS data",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF4D5B6A),
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))
            StatusCard(
                title = "Watch connection",
                value = "Ready to pair",
                accent = Color(0xFF24786D)
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusCard(
                title = "Incoming data",
                value = "Waiting for heart rate and motion streams",
                accent = Color(0xFF4A67A1)
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusCard(
                title = "Part A status",
                value = "Phone module launches successfully",
                accent = Color(0xFF8A5A20)
            )
        }
    }
}

@Composable
private fun StatusCard(title: String, value: String, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .height(42.dp)
                    .weight(0.03f)
                    .background(accent, RoundedCornerShape(6.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF687584)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF16202A),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(
    name = "Phone home",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun PhoneAppPreview() {
    MaterialTheme {
        PhoneApp()
    }
}
