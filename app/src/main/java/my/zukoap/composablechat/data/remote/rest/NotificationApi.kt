package my.zukoap.composablechat.data.remote.rest

import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.domain.entity.notification.NetworkCheckSubscription
import my.zukoap.composablechat.domain.entity.notification.NetworkResultCheckSubscription
import my.zukoap.composablechat.domain.entity.notification.NetworkSubscription
import my.zukoap.composablechat.domain.entity.notification.NetworkUnsubscription
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {

    @POST("webchat/{namespace}/set-user-subscription")
    fun subscribe(
        @Body body: NetworkSubscription,
        @Path("namespace") clientId : String = ChatParams.urlChatNameSpace!!
    ) : Call<Unit>

    @POST("webchat/{namespace}/delete-user-subscription")
    fun unsubscribe(
        @Body body: NetworkUnsubscription,
        @Path("namespace") clientId : String = ChatParams.urlChatNameSpace!!
    ) : Call<Unit>

    @POST("webchat/{namespace}/check-user-subscription")
    fun checkSubscription(
        @Body body: NetworkCheckSubscription,
        @Path("namespace") clientId : String = ChatParams.urlChatNameSpace!!
    ) : Call<NetworkResultCheckSubscription>

}