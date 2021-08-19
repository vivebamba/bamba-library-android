package com.vivebamba.bambalibrary

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pusher.client.connection.ConnectionState
import com.vivebamba.bambalibrary.util.Factory
import com.vivebamba.client.models.AdvisorUser
import com.vivebamba.client.models.Message
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONObject
import java.util.*
import com.vivebamba.bambalibrary.Message as LocalMessage

class ChatActivity : AppCompatActivity() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var advisorUser: AdvisorUser
    private lateinit var bambaService: BambaService
    private var factory: Factory = Factory()
    private var pusher = factory.newPusher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        bambaService = factory.newBambaService()

        messageList.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(this)
        advisorUser = Bamba.getUser()
        messageList.adapter = messageAdapter


        btnSend.setOnClickListener {
            if (!this.hasInternetConnection()) {
                this.showToast(getString(R.string.no_internet))
            } else {
                if (txtMessage.text.isNotEmpty()) {
                   this.sendMessage();
                } else {
                    this.showToast(getString(R.string.no_message))
                }
                setupPusher()
            }
        }
    }

    private fun resetInput() {
        txtMessage.text.clear()
        val inputManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun setupPusher() {
        val connectionEventListener = BroadcastConnectionEventListener();
        pusher.connect(connectionEventListener, ConnectionState.ALL)

        val channel = pusher.subscribe("user-" + advisorUser.uuid + "-channel")

        channel.bind("message-sent") { event ->
            val jsonObject = JSONObject(event.data)

            val message = LocalMessage(
                jsonObject["user"].toString(),
                jsonObject["message"].toString(),
                jsonObject["time"].toString().toLong()
            )

            runOnUiThread {
                messageAdapter.addMessage(message)
                messageList.scrollToPosition(messageAdapter.itemCount - 1);
            }

        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)

        if (caps === null) {
            return false
        }

        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun sendMessage()  {
        val localMessage = LocalMessage(
            advisorUser.cellphone,
            txtMessage.text.toString(),
            Calendar.getInstance().timeInMillis
        )
        val message: Message = Message("text", txtMessage.text.toString())
        try {
            bambaService.sendMessage(message, advisorUser);
            messageAdapter.addMessage(localMessage)
        } catch (e: Exception) {
            println("EXCEPTION: " + e.message)
        }

        this.resetInput()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_SHORT
        )
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        pusher.disconnect()
    }
}