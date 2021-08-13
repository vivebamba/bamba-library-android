package com.vivebamba.bambalibrary

import android.util.Log
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import java.lang.Exception

class BroadcastConnectionEventListener : ConnectionEventListener {
    override fun onConnectionStateChange(change: ConnectionStateChange?) {
        if (change != null) {
            Log.i("Pusher", "State changed from ${change.previousState} to ${change.currentState}")
        }
    }

    override fun onError(message: String?, code: String?, e: Exception?) {
        Log.i(
            "Pusher",
            "There was a problem connecting! code ($code), message ($message), exception($e)"
        )
    }
}