package com.example.assign3.phone

import kotlin.math.sqrt

class SensorProcessor {
    fun processAccelerometer(readings: List<MotionReading>): AccelerometerResult? {
        val latest = readings.lastOrNull() ?: return null
        val movementScore = readings. averageMovementDelta()
        val intensity = when {
            movementScore >= HIGH_ACCELERATION_THRESHOLD -> MotionIntensity.High
            movementScore >= MEDIUM_ACCELERATION_THRESHOLD -> MotionIntensity.Medium
            else -> MotionIntensity.Low
        }

        return AccelerometerResult(
            x = latest.x,
            y = latest.y,
            z = latest.z,
            magnitude = latest.magnitude,
            movementScore = movementScore,
            movementTrend = readings.rollingMovementTrend(),
            intensity = intensity,
        )
    }

    fun processGyroscope(readings: List<MotionReading>): GyroscopeResult? {
        val latest = readings.lastOrNull() ?: return null
        val averageMagnitude = readings.takeLast(MOTION_WINDOW_SIZE).map { it.magnitude }.average().toFloat()
        val movement = if (averageMagnitude >= ACTIVE_ROTATION_THRESHOLD) {
            RotationMovement.Active
        } else {
            RotationMovement.Stable
        }

        return GyroscopeResult(
            x = latest.x,
            y = latest.y,
            z = latest.z,
            rotationMagnitude = latest.magnitude,
            averageRotationMagnitude = averageMagnitude,
            rotationTrend = readings.rollingMagnitudeTrend(),
            movement = movement,
        )
    }

    fun processHeartRate(readings: List<HeartRateReading>): HeartRateResult? {
        val latest = readings.lastOrNull() ?: return null
        //The average of the last three heart rate readings. Since raw heart rate samples from the watch can fluctuate,
        // simple smoothing is applied to reduce noise; the comments note that a small window is used to ensure a responsive user interface.
        val smoothedBpm = readings.takeLast(HEART_RATE_WINDOW_SIZE).map { it.bpm }.average().toFloat()
        val zone = when {
            smoothedBpm >= ELEVATED_HEART_RATE_THRESHOLD -> HeartRateZone.Elevated
            smoothedBpm >= MODERATE_HEART_RATE_THRESHOLD -> HeartRateZone.Moderate
            else -> HeartRateZone.Resting
        }
        //Fold iterates through all historical readings and counts the total number of readings for each interval.
        val distribution = readings.fold(HeartRateDistribution(0, 0, 0)) { current, reading ->
            when {
                reading.bpm >= ELEVATED_HEART_RATE_THRESHOLD -> current.copy(elevated = current.elevated + 1)
                reading.bpm >= MODERATE_HEART_RATE_THRESHOLD -> current.copy(moderate = current.moderate + 1)
                else -> current.copy(resting = current.resting + 1)
            }
        }

        return HeartRateResult(
            bpm = latest.bpm,
            smoothedBpm = smoothedBpm,
            zone = zone,
            zoneDistribution = distribution,
        )
    }
}

//Calculating the average distance between all adjacent frames yields the `movementScore`,
//which represents the intensity of body shaking or movement.
private fun List<MotionReading>.averageMovementDelta(): Float {
    val recentReadings = takeLast(MOTION_WINDOW_SIZE + 1)
    if (recentReadings.size < 2) return 0f

    return recentReadings
        .zipWithNext { previous, current -> previous.distanceTo(current) }
        .average()
        .toFloat()
}

//Output a `List<Float>` directly to the UI line chart:
//Iterate through the most recent 30 data points;
//at each step, take the first `index + 1` points to calculate the average activity score,
//thereby generating a sequence of points representing the curve over time.
private fun List<MotionReading>.rollingMovementTrend(): List<Float> {
    val recentReadings = takeLast(CHART_WINDOW_SIZE)
    return recentReadings.indices.map { index ->
        recentReadings
            .take(index + 1)
            .averageMovementDelta()
    }
}

// Raw accelerometer magnitude includes gravity, so recent x/y/z change is a clearer movement feature.
private fun MotionReading.distanceTo(other: MotionReading): Float {
    val dx = other.x - x
    val dy = other.y - y
    val dz = other.z - z
    return sqrt(dx * dx + dy * dy + dz * dz)
}

//Generating the plot sequence: For each time point, calculate the mean magnitude of the gyroscope readings over a short window and output the curve data.
//Distinction from the acceleration trend: The gyroscope uses its own magnitude directly,
//eliminating the need to calculate the distance between two points;
//furthermore, the gyroscope outputs angular velocity, which approaches zero when the device is stationary.
private fun List<MotionReading>.rollingMagnitudeTrend(): List<Float> {
    val recentReadings = takeLast(CHART_WINDOW_SIZE)
    //`indices` is a property of a collection or list that returns a range of all the indices within that list.
    return recentReadings.indices.map { index ->
        recentReadings
            .take(index + 1)
            .takeLast(MOTION_WINDOW_SIZE)
            .map { it.magnitude }
            .average()
            .toFloat()
    }
}

private const val MOTION_WINDOW_SIZE = 10
// A short heart-rate window keeps the display responsive while smoothing small callback noise.
private const val HEART_RATE_WINDOW_SIZE = 3
private const val CHART_WINDOW_SIZE = 30
private const val MEDIUM_ACCELERATION_THRESHOLD = 0.6
private const val HIGH_ACCELERATION_THRESHOLD = 1.6
private const val ACTIVE_ROTATION_THRESHOLD = 1.0
private const val MODERATE_HEART_RATE_THRESHOLD = 90.0
private const val ELEVATED_HEART_RATE_THRESHOLD = 120.0
