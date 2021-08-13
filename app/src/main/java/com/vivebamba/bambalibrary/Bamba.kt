package com.vivebamba.bambalibrary

import android.content.Context
import android.content.Intent
import com.vivebamba.client.models.AdvisorUser
import com.vivebamba.bambalibrary.util.Factory

object Bamba : Client {
    var factory = Factory()
    var apiKey = ""
    var broadcastApiKey = ""
    private lateinit var user: AdvisorUser

    override fun openchat(context: Context) {
        val intent = Intent(context, ChatActivity::class.java)
        context.startActivity(intent);
    }

    override fun user(name: String, lastName: String, cellphone: String, uuid: String) {
        user = factory.newBambaUser(name, lastName, cellphone, uuid)
    }

    override fun getUser(): AdvisorUser {
        return user;
    }
}