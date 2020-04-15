package com.shoot

import android.util.Log
import androidx.collection.SimpleArrayMap
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.shoot.engine.*
import com.shoot.engine.Main.Companion.enemy
import com.shoot.engine.Main.Companion.player
import java.io.IOException


class DataTransfer : PayloadCallback() {
    var firstConnection = false

    override fun onPayloadTransferUpdate(
        endPointID: String,
        payloadTransferUpdate: PayloadTransferUpdate
    ) {

    }

    override fun onPayloadReceived(endPointID: String, payload: Payload) {
        try {
            val temp = payload.asBytes()?.let { MainActivity.deserialize(it) }
            Log.e("DATA", "RECEIVED")
            when (temp) {
                is Player -> enemy.set(temp.toValue())
                is DynamicData -> when (temp.TAG) {
                    "enemy" -> {
                        enemy.update(temp.toValue())
                        Log.e("Shoot", "Updating Enemy")
                    }
                    "player" -> {
                        player.update(temp.toValue())
                        Log.e("Shoot", "Updating Player")

                    }

                }

            }
        } catch (exception: IOException) {
            Log.e("Shoot", exception.toString())
        } catch (exception: ClassNotFoundException) {
            Log.e("Shoot", exception.toString())
        }
    }
}