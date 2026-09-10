package com.example.assign3.phone

import java.util.Locale

class SensorProcessor {
    fun processAccelerometer(readings: List<MotionReading>): String {
        val latest = readings.lastOrNull() ?: return "Waiting for watch data"
        val averageMagnitude = readings.takeLast(MOTION_WINDOW_SIZE).map { it.magnitude }.average()
        val intensity = when {
            averageMagnitude >= HIGH_ACCELERATION_THRESHOLD -> "High"
            averageMagnitude >= MEDIUM_ACCELERATION_THRESHOLD -> "Medium"
            else -> "Low"
        }

        return String.format(
            Locale.US,
            "X: %.2f  Y: %.2f  Z: %.2f\nMagnitude: %.2f\nAvg: %.2f  Intensity: %s",
            latest.x,
            latest.y,
            latest.z,
            latest.magnitude,
            averageMagnitude,
            intensity,
        )
    }

    fun processGyroscope(readings: List<MotionReading>): String {
        val latest = readings.lastOrNull() ?: return "Waiting for gyroscope data"
        val averageMagnitude = readings.takeLast(MOTION_WINDOW_SIZE).map { it.magnitude }.average()
        val movement = if (averageMagnitude >= ACTIVE_ROTATION_THRESHOLD) {
            "Active"
        } else {
            "Stable"
        }

        return String.format(
            Locale.US,
            "X: %.2f  Y: %.2f  Z: %.2f\nRotation: %.2f\nAvg: %.2f  Movement: %s",
            latest.x,
            latest.y,
            latest.z,
            latest.magnitude,
            averageMagnitude,
            movement,
        )
    }

    fun processHeartRate(readings: List<HeartRateReading>): String {
        val latest = readings.lastOrNull() ?: return "Waiting for heart rate data"
        val smoothedBpm = readings.takeLast(HEART_RATE_WINDOW_SIZE).map { it.bpm }.average()
        val zone = when {
            smoothedBpm >= ELEVATED_HEART_RATE_THRESHOLD -> "Elevated"
            smoothedBpm >= MODERATE_HEART_RATE_THRESHOLD -> "Moderate"
            else -> "Resting"
        }

        return String.format(
            Locale.US,
            "BPM: %.0f\nSmoothed: %.0f\nZone: %s",
            latest.bpm,
            smoothedBpm,
            zone,
        )
    }
}

private const val MOTION_WINDOW_SIZE = 10
private const val HEART_RATE_WINDOW_SIZE = 5
private const val MEDIUM_ACCELERATION_THRESHOLD = 11.0
private const val HIGH_ACCELERATION_THRESHOLD = 15.0
private const val ACTIVE_ROTATION_THRESHOLD = 1.0
private const val MODERATE_HEART_RATE_THRESHOLD = 90.0
private const val ELEVATED_HEART_RATE_THRESHOLD = 120.0
