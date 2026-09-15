package com.example.assign3.wear.watch

import android.annotation.SuppressLint
import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.health.connect.HealthPermissions
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import java.util.Locale


class SensorActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var measureClient: MeasureClient

    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private var isAccelerometerAvailable by mutableStateOf(true)
    private var isGyroscopeAvailable by mutableStateOf(true)

    private var accelerometerX by mutableFloatStateOf(0f)
    private var accelerometerY by mutableFloatStateOf(0f)
    private var accelerometerZ by mutableFloatStateOf(0f)
    private var lastAccelerometerSendTime = 0L

    private var gyroscopeX by mutableFloatStateOf(0f)
    private var gyroscopeY by mutableFloatStateOf(0f)
    private var gyroscopeZ by mutableFloatStateOf(0f)
    private var lastGyroscopeSendTime = 0L

    private var heartRateBpm by mutableFloatStateOf(0f)
    private var supportsHeartRate by mutableStateOf(false)
    private var isHeartRateAvailable by mutableStateOf(false)
    private var isHeartRateRegistered = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val healthClient = HealthServices.getClient(this)
        measureClient = healthClient.measureClient

        lifecycleScope.launch {
            val capabilities = measureClient.getCapabilitiesAsync().await()
            supportsHeartRate =
                DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure
            registerHeartRateMeasure()
        }

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        isAccelerometerAvailable = accelerometer != null
        isGyroscopeAvailable = gyroscope != null

        setContent {
            SensorApp(
                accelerometerX = accelerometerX,
                accelerometerY = accelerometerY,
                accelerometerZ = accelerometerZ,
                gyroscopeX = gyroscopeX,
                gyroscopeY = gyroscopeY,
                gyroscopeZ = gyroscopeZ,

                isAccelerometerAvailable = isAccelerometerAvailable,
                isGyroscopeAvailable = isGyroscopeAvailable,

                heartRateBpm = heartRateBpm,
                isHeartRateAvailable = isHeartRateAvailable,
            )
        }

        if (!hasHeartRatePermission()) {
            heartRatePermissionLauncher.launch(heartRatePermission())
        }
    }

    override fun onResume() {
        super.onResume()
        listOfNotNull(accelerometer, gyroscope).forEach {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
            )
        }
        registerHeartRateMeasure()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        unregisterHeartRateMeasure()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val sensorEvent = event ?: return
        when (sensorEvent.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelerometerX = sensorEvent.values[0]
                accelerometerY = sensorEvent.values[1]
                accelerometerZ = sensorEvent.values[2]
                sendAccelerometerToPhone(
                    x = accelerometerX,
                    y = accelerometerY,
                    z = accelerometerZ,
                )
            }

            Sensor.TYPE_GYROSCOPE -> {
                gyroscopeX = sensorEvent.values[0]
                gyroscopeY = sensorEvent.values[1]
                gyroscopeZ = sensorEvent.values[2]
                sendGyroscopeToPhone(
                    x = gyroscopeX,
                    y = gyroscopeY,
                    z = gyroscopeZ,
                )
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun sendAccelerometerToPhone(x: Float, y: Float, z: Float) {
        val now = SystemClock.elapsedRealtime()
        // Motion sensors can fire frequently, so messages are rate-limited for a smoother phone display.
        if (now - lastAccelerometerSendTime < ACCELEROMETER_SEND_INTERVAL_MS) {
            return
        }
        lastAccelerometerSendTime = now
        sendMotionToPhone(
            path = ACCELEROMETER_MESSAGE_PATH,
            x = x,
            y = y,
            z = z,
        )
    }

    private fun sendGyroscopeToPhone(x: Float, y: Float, z: Float) {
        val now = SystemClock.elapsedRealtime()
        // Gyroscope uses the same lightweight message path as accelerometer because it is time-sensitive motion data.
        if (now - lastGyroscopeSendTime < GYROSCOPE_SEND_INTERVAL_MS) {
            return
        }
        lastGyroscopeSendTime = now
        sendMotionToPhone(
            path = GYROSCOPE_MESSAGE_PATH,
            x = x,
            y = y,
            z = z,
        )
    }

    private fun sendMotionToPhone(path: String, x: Float, y: Float, z: Float) {
        val payload = String.format(
            Locale.US,
            "%d,%.4f,%.4f,%.4f",
            System.currentTimeMillis(),
            x,
            y,
            z,
        ).toByteArray(Charsets.UTF_8)

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isEmpty()) {
                Log.w(TAG, "No connected phone nodes available for $path")
            }
            nodes.forEach { node ->
                Wearable.getMessageClient(this).sendMessage(
                    node.id,
                    path,
                    payload,
                ).addOnFailureListener { error ->
                    Log.w(TAG, "Failed to send motion data to ${node.displayName}", error)
                }
            }
        }.addOnFailureListener { error ->
            Log.w(TAG, "Failed to get connected phone nodes", error)
        }
    }

    @SuppressLint("VisibleForTests")
    private fun sendHeartRateToPhone(bpm: Float) {
        // Heart rate is lower frequency, so DataClient stores it as the latest state for the phone.
        val dataRequest = PutDataMapRequest.create(HEART_RATE_DATA_PATH).apply {
            dataMap.putLong("timestamp", System.currentTimeMillis())
            dataMap.putFloat("bpm", bpm)
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(this).putDataItem(dataRequest)
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to send heart rate data", error)
            }
    }


    private val heartRatePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                registerHeartRateMeasure()
            } else {
                isHeartRateAvailable = false
            }
        }
    private fun hasHeartRatePermission(): Boolean {
        return checkSelfPermission(heartRatePermission()) == PackageManager.PERMISSION_GRANTED
    }

    private fun registerHeartRateMeasure() {
        if (!supportsHeartRate || !hasHeartRatePermission() || isHeartRateRegistered) {
            return
        }
        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, heartRateCallback)
        isHeartRateRegistered = true
    }

    private fun unregisterHeartRateMeasure() {
        if (!isHeartRateRegistered) {
            return
        }
        lifecycleScope.launch {
            measureClient.unregisterMeasureCallbackAsync(
                DataType.HEART_RATE_BPM,
                heartRateCallback,
            ).await()
        }
        isHeartRateRegistered = false
    }

    private val heartRateCallback = object : MeasureCallback {
        override fun onAvailabilityChanged(
            dataType: DeltaDataType<*, *>,
            availability: Availability,
        ) {
        }

        override fun onDataReceived(data: DataPointContainer) {
            val heartRateData = data.getData(DataType.HEART_RATE_BPM)
            val latestHeartRate = heartRateData.lastOrNull()?.value
            if (latestHeartRate != null) {
                sendHeartRateToPhone(latestHeartRate.toFloat())
                runOnUiThread {
                    heartRateBpm = latestHeartRate.toFloat()
                    isHeartRateAvailable = true
                }
            }
        }

        override fun onRegistrationFailed(throwable: Throwable) {
            runOnUiThread {
                isHeartRateRegistered = false
                isHeartRateAvailable = false
            }
        }
    }
}

private fun heartRatePermission(): String {
    return if (Build.VERSION.SDK_INT >= 36) {
        HealthPermissions.READ_HEART_RATE
    } else {
        Manifest.permission.BODY_SENSORS
    }
}

private const val ACCELEROMETER_MESSAGE_PATH = "/motion/accelerometer"
private const val GYROSCOPE_MESSAGE_PATH = "/motion/gyroscope"
private const val HEART_RATE_DATA_PATH = "/health/heart_rate"
private const val ACCELEROMETER_SEND_INTERVAL_MS = 500L
private const val GYROSCOPE_SEND_INTERVAL_MS = 500L
private const val TAG = "SensorActivity"
