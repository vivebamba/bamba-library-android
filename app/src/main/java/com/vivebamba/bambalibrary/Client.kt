package com.vivebamba.bambalibrary

import android.content.Context
import com.vivebamba.client.models.AdvisorUser

interface Client {
    public fun openchat(context: Context)
    public fun user(name: String, lastName: String, cellphone: String, uuid: String)
    public fun getUser(): AdvisorUser
}