package com.example.assign3.phone

data class MotionReading(
    val timestamp: Long,
    val x: Float,
    val y: Float,
    val z: Float,
) {
    val magnitude: Float
        get() = kotlin.math.sqrt(x * x + y * y + z * z)
}

data class HeartRateReading(
    val timestamp: Long,
    val bpm: Float,
)
