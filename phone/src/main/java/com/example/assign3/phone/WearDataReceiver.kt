package com.example.assign3.phone

import android.content.Context
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.util.Locale

class WearDataReceiver(
    context: Context,
    private val onAccelerometerReceived: (String) -> Unit,
) : MessageClient.OnMessageReceivedListener {
    private val messageClient = Wearable.getMessageClient(context)

    fun start() {
        messageClient.addListener(this)
    }

    fun stop() {
        messageClient.removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path != ACCELEROMETER_MESSAGE_PATH) {
            return
        }

        val message = messageEvent.data.toString(Charsets.UTF_8)
        val parts = message.split(",")
        if (parts.size != 4) {
            return
        }

        val x = parts[1].toFloatOrNull() ?: return
        val y = parts[2].toFloatOrNull() ?: return
        val z = parts[3].toFloatOrNull() ?: return

        onAccelerometerReceived(
            String.format(
                Locale.US,
                "Accelerometer  X: %.2f  Y: %.2f  Z: %.2f",
                x,
                y,
                z,
            )
        )
    }
}

private const val ACCELEROMETER_MESSAGE_PATH = "/motion/accelerometer"
