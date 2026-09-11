package com.example.assign3.phone

import java.util.Locale

fun AccelerometerResult.toDisplayText(): String {
    return String.format(
        Locale.US,
        "Raw X: %.2f  Y: %.2f  Z: %.2f\nRaw magnitude: %.2f\nMovement score: %.2f  Intensity: %s",
        x,
        y,
        z,
        magnitude,
        movementScore,
        intensity,
    )
}

fun GyroscopeResult.toDisplayText(): String {
    return String.format(
        Locale.US,
        "Raw X: %.2f  Y: %.2f  Z: %.2f\nRotation magnitude: %.2f\nRolling avg: %.2f  Movement: %s",
        x,
        y,
        z,
        rotationMagnitude,
        averageRotationMagnitude,
        movement,
    )
}

fun HeartRateResult.toDisplayText(): String {
    return String.format(
        Locale.US,
        "Latest BPM: %.0f\nSmoothed BPM: %.0f\nZone: %s",
        bpm,
        smoothedBpm,
        zone,
    )
}
