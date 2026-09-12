package com.example.assign3.phone

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class WearDataReceiver(
    context: Context,
    private val onAccelerometerReceived: (MotionReading) -> Unit,
    private val onGyroscopeReceived: (MotionReading) -> Unit,
    private val onHeartRateReceived: (HeartRateReading) -> Unit,
    //Google Wearable Companion SDK，
    // messageClient(short messages, sent as one-off transmissions.)，
    // dataClient(transmits heart rate data; suitable for synchronizing state data.)
) : MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener {
    private val appContext = context.applicationContext
    private val messageClient = Wearable.getMessageClient(appContext)
    private val dataClient = Wearable.getDataClient(appContext)

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
            ACCELEROMETER_MESSAGE_PATH -> parseMotionMessage(messageEvent.data)?.let {
                onAccelerometerReceived(it)
            }
            GYROSCOPE_MESSAGE_PATH -> parseMotionMessage(messageEvent.data)?.let {
                onGyroscopeReceived(it)
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == HEART_RATE_DATA_PATH
            ) {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val timestamp = dataMap.getLong("timestamp")
                val bpm = dataMap.getFloat("bpm")
                onHeartRateReceived(HeartRateReading(timestamp = timestamp, bpm = bpm))
            }
        }
    }

    private fun parseMotionMessage(data: ByteArray): MotionReading? {
        val message = data.toString(Charsets.UTF_8)
        val parts = message.split(",")
        if (parts.size != 4) {
            return null
        }

        val timestamp = parts[0].toLongOrNull() ?: return null
        val x = parts[1].toFloatOrNull() ?: return null
        val y = parts[2].toFloatOrNull() ?: return null
        val z = parts[3].toFloatOrNull() ?: return null

        return MotionReading(timestamp = timestamp, x = x, y = y, z = z)
    }
}

private const val ACCELEROMETER_MESSAGE_PATH = "/motion/accelerometer"
private const val GYROSCOPE_MESSAGE_PATH = "/motion/gyroscope"
private const val HEART_RATE_DATA_PATH = "/health/heart_rate"
