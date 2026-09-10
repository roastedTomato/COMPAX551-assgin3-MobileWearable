package com.example.assign3.phone

data class PhoneUiState(
    val accelerometerText: String = "Waiting for watch data",
    val gyroscopeText: String = "Waiting for gyroscope data",
    val heartRateText: String = "Waiting for heart rate data",
)
