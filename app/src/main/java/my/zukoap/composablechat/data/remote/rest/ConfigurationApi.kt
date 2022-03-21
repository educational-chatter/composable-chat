package my.zukoap.composablechat.data.remote.rest

import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.domain.entity.configuration.NetworkResultConfiguration
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ConfigurationApi {

    @GET("configuration/{clientId}")
    fun getConfiguration(
        @Path("clientId") clientId: String = ChatParams.urlChatNameSpace!!
    ): Call<NetworkResultConfiguration>

}