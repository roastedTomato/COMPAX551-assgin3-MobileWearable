package com.example.assign3.phone

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PhoneScreen(
    uiState: PhoneUiState,
    onResetSession: () -> Unit,
) {
    var selectedSensor by remember { mutableStateOf(SensorPage.Accelerometer) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "A3 - Phone App",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Qianyu CAO",
            fontSize = 18.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
        )
        SensorSelector(
            selectedSensor = selectedSensor,
            onSensorSelected = { selectedSensor = it },
        )
        SessionStatusRow(
            isReceiving = uiState.isReceivingWatchData,
            onResetSession = onResetSession,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 100.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (selectedSensor) {
                SensorPage.Accelerometer -> AccelerometerSection(
                    title = "Accelerometer",
                    result = uiState.accelerometer,
                )

                SensorPage.Gyroscope -> GyroscopeSection(
                    title = "Gyroscope",
                    result = uiState.gyroscope,
                )

                SensorPage.HeartRate -> HeartRateSection(
                    title = "Heart Rate",
                    result = uiState.heartRate,
                )
            }
        }
    }
}

@Composable
private fun SessionStatusRow(
    isReceiving: Boolean,
    onResetSession: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = if (isReceiving) "Receiving watch data" else "Waiting for watch",
            color = if (isReceiving) CalmGreen else Color(0xFF666666),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Button(
            onClick = onResetSession,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF5F5F5),
                contentColor = Color.Black,
            ),
        ) {
            Text(text = "Reset", fontSize = 14.sp)
        }
    }
}

@Composable
private fun SensorSelector(
    selectedSensor: SensorPage,
    onSensorSelected: (SensorPage) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SensorPage.entries.forEachIndexed { index, page ->
            SegmentedButton(
                selected = selectedSensor == page,
                onClick = { onSensorSelected(page) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = SensorPage.entries.size,
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Color(0xFFEAF7EA),
                    activeContentColor = Color.Black,
                    activeBorderColor = Color(0xFFEAF7EA),
                    inactiveContainerColor = Color(0xFFF5F5F5),
                    inactiveContentColor = Color.Black,
                    inactiveBorderColor = Color(0xFFF5F5F5),
                ),
                modifier = Modifier.defaultMinSize(minHeight = 52.dp),
            ) {
                Text(text = page.label, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun AccelerometerSection(
    title: String,
    result: AccelerometerResult?,
) {
    val color = when (result?.intensity) {
        MotionIntensity.Medium -> Amber
        MotionIntensity.High -> AlertRed
        else -> CalmGreen
    }

    PhoneDataSection(
        title = title,
        primaryValue = result?.intensity?.name ?: "Waiting",
        value = result?.displayText ?: "Waiting for watch data",
        primaryColor = color,
    ) {
        TimeSeriesLineChart(
            values = result?.movementTrend.orEmpty(),
            max = ACCELEROMETER_MOVEMENT_MAX,
            color = color,
            yLabel = "Movement score (0-3)",
        )
    }
}

@Composable
private fun GyroscopeSection(
    title: String,
    result: GyroscopeResult?,
) {
    val color = when (result?.movement) {
        RotationMovement.Active -> Amber
        else -> CalmGreen
    }

    PhoneDataSection(
        title = title,
        primaryValue = result?.movement?.name ?: "Waiting",
        value = result?.displayText ?: "Waiting for gyroscope data",
        primaryColor = color,
    ) {
        TimeSeriesLineChart(
            values = result?.rotationTrend.orEmpty(),
            max = GYROSCOPE_MAX,
            color = color,
            yLabel = "Rotation magnitude (0-5)",
        )
    }
}

@Composable
private fun HeartRateSection(
    title: String,
    result: HeartRateResult?,
) {
    val color = when (result?.zone) {
        HeartRateZone.Moderate -> Amber
        HeartRateZone.Elevated -> AlertRed
        else -> CalmGreen
    }

    PhoneDataSection(
        title = title,
        primaryValue = result?.let { "${it.smoothedBpm.roundToInt()} BPM" } ?: "Waiting",
        value = result?.displayText ?: "Waiting for heart rate data",
        primaryColor = color,
    ) {
        HeartRateGauge(
            progress = result?.smoothedBpm.asHeartRateProgress(),
            bpm = result?.smoothedBpm,
            color = color,
        )
        Spacer(modifier = Modifier.height(8.dp))
        ZoneDistributionBar(distribution = result?.zoneDistribution)
    }
}

@Composable
private fun PhoneDataSection(
    title: String,
    primaryValue: String,
    value: String,
    primaryColor: Color,
    visualisation: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFEAF7EA),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(vertical = 18.dp, horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = primaryValue,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = primaryColor,
        )
        Spacer(modifier = Modifier.height(14.dp))
        visualisation()
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = value,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun TimeSeriesLineChart(
    values: List<Float>,
    max: Float,
    color: Color,
    yLabel: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .width(34.dp)
                    .fillMaxHeight()
                    .padding(end = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
            ) {
                Text(text = max.axisLabel(), color = Color.Black, fontSize = 12.sp)
                Text(text = "0", color = Color.Black, fontSize = 12.sp)
            }

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                val baseline = size.height
                drawLine(
                    color = Color(0xFFDDDDDD),
                    start = Offset(0f, baseline),
                    end = Offset(size.width, baseline),
                    strokeWidth = 1.dp.toPx(),
                )

                if (values.isEmpty()) return@Canvas

                val chartValues = values.map { value -> (value / max).coerceIn(0f, 1f) }
                if (chartValues.size == 1) {
                    val y = size.height - chartValues.first() * size.height
                    drawCircle(color = color, radius = 5.dp.toPx(), center = Offset(size.width, y))
                    return@Canvas
                }

                val path = Path()
                chartValues.forEachIndexed { index, value ->
                    val x = size.width * index / (chartValues.lastIndex)
                    val y = size.height - value * size.height
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "t-30", color = Color.Black, fontSize = 12.sp)
            Text(
                text = yLabel,
                color = Color.Black,
                fontSize = 12.sp,
            )
            Text(text = "now", color = Color.Black, fontSize = 12.sp)
        }
    }
}

private fun Float.axisLabel(): String {
    return if (this % 1f == 0f) {
        roundToInt().toString()
    } else {
        String.format(Locale.US, "%.1f", this)
    }
}

@Composable
private fun HeartRateGauge(
    progress: Float,
    bpm: Float?,
    color: Color,
) {
    Box(
        modifier = Modifier.size(116.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(104.dp)) {
            val strokeWidth = 12.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        Text(
            text = bpm?.roundToInt()?.toString() ?: "--",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ZoneDistributionBar(distribution: HeartRateDistribution?) {
    val zoneDistribution = distribution ?: HeartRateDistribution(0, 0, 0)
    val total = zoneDistribution.total

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(Color.White, RoundedCornerShape(6.dp))
        ) {
            if (total == 0) return@Canvas

            var startX = 0f
            listOf(
                zoneDistribution.resting to CalmGreen,
                zoneDistribution.moderate to Amber,
                zoneDistribution.elevated to AlertRed,
            ).forEach { (count, color) ->
                val segmentWidth = size.width * count / total
                drawRect(
                    color = color,
                    topLeft = Offset(startX, 0f),
                    size = Size(segmentWidth, size.height),
                )
                startX += segmentWidth
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ZoneLabel("Rest", zoneDistribution.resting, total, CalmGreen)
            ZoneLabel("Mod", zoneDistribution.moderate, total, Amber)
            ZoneLabel("Elev", zoneDistribution.elevated, total, AlertRed)
        }
    }
}

@Composable
private fun ZoneLabel(
    label: String,
    count: Int,
    total: Int,
    color: Color,
) {
    val percent = if (total == 0) 0 else (count * 100f / total).roundToInt()
    Text(
        text = "$label $percent%",
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    )
}

private fun Float?.asHeartRateProgress(): Float {
    return (((this ?: HEART_RATE_MIN) - HEART_RATE_MIN) / (HEART_RATE_MAX - HEART_RATE_MIN))
        .coerceIn(0f, 1f)
}

private val CalmGreen = Color(0xFF2E7D32)
private val Amber = Color(0xFFF9A825)
private val AlertRed = Color(0xFFC62828)
private const val ACCELEROMETER_MOVEMENT_MAX = 3f
private const val GYROSCOPE_MAX = 5f
private const val HEART_RATE_MIN = 40f
private const val HEART_RATE_MAX = 180f

private enum class SensorPage(val label: String) {
    Accelerometer("Accel"),
    Gyroscope("Gyro"),
    HeartRate("Heart"),
}

@Preview
@Composable
private fun PhoneScreenPreview() {
    MaterialTheme {
        PhoneScreen(
            uiState = PhoneUiState(
                accelerometer = AccelerometerResult(
                    x = 0.12f,
                    y = 9.81f,
                    z = -0.34f,
                    magnitude = 9.82f,
                    movementScore = 1.1f,
                    movementTrend = listOf(0.1f, 0.3f, 0.7f, 1.1f, 1.4f, 0.9f, 1.2f),
                    intensity = MotionIntensity.Medium,
                    displayText = "Raw X: 0.12  Y: 9.81  Z: -0.34\nRaw magnitude: 9.82\nMovement score: 1.10  Intensity: Medium",
                ),
                gyroscope = GyroscopeResult(
                    x = 0.01f,
                    y = -0.02f,
                    z = 0.03f,
                    rotationMagnitude = 0.04f,
                    averageRotationMagnitude = 1.2f,
                    rotationTrend = listOf(0.2f, 0.4f, 0.7f, 1.1f, 1.2f, 0.9f, 1.5f),
                    movement = RotationMovement.Active,
                    displayText = "Raw X: 0.01  Y: -0.02  Z: 0.03\nRotation magnitude: 0.04\nRolling avg: 1.20  Movement: Active",
                ),
                heartRate = HeartRateResult(
                    bpm = 96f,
                    smoothedBpm = 94f,
                    zone = HeartRateZone.Moderate,
                    zoneDistribution = HeartRateDistribution(
                        resting = 8,
                        moderate = 14,
                        elevated = 3,
                    ),
                    displayText = "Latest BPM: 96\nSmoothed BPM: 94\nZone: Moderate",
                ),
                isReceivingWatchData = true,
            ),
            onResetSession = {},
        )
    }
}
