package com.vivebamba.bambalibrary.util

import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.vivebamba.client.models.AdvisorUser
import com.vivebamba.bambalibrary.Bamba
import com.vivebamba.bambalibrary.BambaService

class Factory {
    fun newBambaService(): BambaService {
        return BambaService()
    }

    fun newPusher(): Pusher {
        val options = PusherOptions()
        options.setCluster("mt1");
        return  Pusher(Bamba.broadcastApiKey, options)
    }

    fun newBambaUser(name: String, lastName: String, cellphone: String, uuid: String): AdvisorUser
    {
        return AdvisorUser(
            name,
            lastName,
            cellphone,
            uuid
        )
    }
}