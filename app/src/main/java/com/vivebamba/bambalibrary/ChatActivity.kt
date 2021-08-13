package com.vivebamba.bambalibrary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pusher.client.connection.ConnectionState
import com.vivebamba.client.models.AdvisorUser
import com.vivebamba.client.models.Message
import com.vivebamba.bambalibrary.util.Factory
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONObject
import java.util.*
import com.vivebamba.bambalibrary.Message as LocalMessage

class ChatActivity : AppCompatActivity() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var advisorUser: AdvisorUser
    private var factory: Factory = Factory()
    private var pusher = factory.newPusher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val bambaService = factory.newBambaService()

        messageList.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(this)
        advisorUser = Bamba.getUser()
        messageList.adapter = messageAdapter


        btnSend.setOnClickListener {
            if (txtMessage.text.isNotEmpty()) {
                val localMessage = LocalMessage(
                    advisorUser.cellphone,
                    txtMessage.text.toString(),
                    Calendar.getInstance().timeInMillis
                )
                val message: Message = Message("text", txtMessage.text.toString())
                messageAdapter.addMessage(localMessage)
                try {
                    bambaService.sendMessage(message, advisorUser);
                } catch (e: Exception) {
                    println("EXCEPTION: " + e.message)
                }

                this.resetInput()
            } else {
                Toast.makeText(
                    applicationContext,
                    "No haz tecleado un mensaje",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        setupPusher()
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
            Log.i("Pusher", "Received event with data: $event")
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

    override fun onDestroy() {
        super.onDestroy()
        pusher.disconnect()
    }
}