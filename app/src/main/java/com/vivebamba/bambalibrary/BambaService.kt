package com.vivebamba.bambalibrary

import com.vivebamba.client.apis.BambaAdvisorApi
import com.vivebamba.client.infrastructure.ApiClient
import com.vivebamba.client.models.AdvisorMessageRequest
import com.vivebamba.client.models.AdvisorUser
import com.vivebamba.client.models.Message

class BambaService() {

    init {
        ApiClient.apiKey["x-api-key"] = Bamba.apiKey
    }

    fun sendMessage(message: Message, advisorUser: AdvisorUser) {
        val bambaAdvisorApi: BambaAdvisorApi = BambaAdvisorApi()
        val advisorMessageRequest: AdvisorMessageRequest =
            AdvisorMessageRequest(advisorUser, message)
        bambaAdvisorApi.advisorMessagePost(advisorMessageRequest);
    }
}