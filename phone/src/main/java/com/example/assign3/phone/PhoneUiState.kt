package com.example.assign3.phone

data class PhoneUiState(
    val accelerometer: AccelerometerResult? = null,
    val gyroscope: GyroscopeResult? = null,
    val heartRate: HeartRateResult? = null,
    val isReceivingWatchData: Boolean = false,
)

data class AccelerometerResult(
    val x: Float,
    val y: Float,
    val z: Float,
    val magnitude: Float,
    val movementScore: Float,
    val movementTrend: List<Float>,
    val intensity: MotionIntensity,
)

data class GyroscopeResult(
    val x: Float,
    val y: Float,
    val z: Float,
    val rotationMagnitude: Float,
    val averageRotationMagnitude: Float,
    val rotationTrend: List<Float>,
    val movement: RotationMovement,
)

data class HeartRateResult(
    val bpm: Float,
    val smoothedBpm: Float,
    val bpmTrend: List<Float>,
    val zone: HeartRateZone,
    val zoneDistribution: HeartRateDistribution,
)

//Counts how many heart rate readings fall into each zone
data class HeartRateDistribution(
    val resting: Int,
    val moderate: Int,
    val elevated: Int,
) {
    val total: Int
        get() = resting + moderate + elevated
}

enum class MotionIntensity {
    Low,
    Medium,
    High,
}

enum class RotationMovement {
    Stable,
    Active,
}

enum class HeartRateZone {
    Resting,
    Moderate,
    Elevated,
}
