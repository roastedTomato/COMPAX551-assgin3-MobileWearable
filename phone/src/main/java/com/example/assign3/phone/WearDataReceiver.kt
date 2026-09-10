package com.example.assign3.phone

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.util.Locale

class WearDataReceiver(
    context: Context,
    private val onAccelerometerReceived: (String) -> Unit,
    private val onGyroscopeReceived: (String) -> Unit,
    private val onHeartRateReceived: (String) -> Unit,
) : MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener {
    private val messageClient = Wearable.getMessageClient(context)
    private val dataClient = Wearable.getDataClient(context)

    fun start() {
        messageClient.addListener(this)
        dataClient.addListener(this)
    }

    fun stop() {
        messageClient.removeListener(this)
        dataClient.removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            ACCELEROMETER_MESSAGE_PATH -> onAccelerometerReceived(
                formatMotionMessage("Accelerometer", messageEvent.data)
            )
            GYROSCOPE_MESSAGE_PATH -> onGyroscopeReceived(
                formatMotionMessage("Gyroscope", messageEvent.data)
            )
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == HEART_RATE_DATA_PATH
            ) {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val bpm = dataMap.getFloat("bpm")
                onHeartRateReceived(
                    String.format(Locale.US, "Heart Rate  BPM: %.0f", bpm)
                )
            }
        }
    }

    private fun formatMotionMessage(label: String, data: ByteArray): String {
        val message = data.toString(Charsets.UTF_8)
        val parts = message.split(",")
        if (parts.size != 4) {
            return "$label data unavailable"
        }

        val x = parts[1].toFloatOrNull() ?: return "$label data unavailable"
        val y = parts[2].toFloatOrNull() ?: return "$label data unavailable"
        val z = parts[3].toFloatOrNull() ?: return "$label data unavailable"

        return String.format(
            Locale.US,
            "%s  X: %.2f  Y: %.2f  Z: %.2f",
            label,
            x,
            y,
            z,
        )
    }
}

private const val ACCELEROMETER_MESSAGE_PATH = "/motion/accelerometer"
private const val GYROSCOPE_MESSAGE_PATH = "/motion/gyroscope"
private const val HEART_RATE_DATA_PATH = "/health/heart_rate"
