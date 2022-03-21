package my.zukoap.composablechat.data.remote.rest

import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.domain.entity.message.NetworkMessage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApi {

    @GET("webhooks/webchat/{namespace}/message_feed")
    fun uploadMessages(
        @Path("namespace") clientId : String = ChatParams.urlChatNameSpace!!,
        @Query("visitor_uuid") uuid: String,
        @Query("last_timestamp") timestamp: Long,
        @Query("message_count") messageCount: Int = ChatParams.countDownloadedMessages,
        @Query("from_active_dialog") fromActiveDialog: Int = 0
    ) : Call<List<NetworkMessage>>

}