package my.zukoap.composablechat.data.remote.rest

import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.domain.entity.person.NetworkResultPersonPreview
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonApi {

    @GET("webchat/{clientId}/get-operator/{personId}")
    fun getPersonPreview(
        @Path("clientId") clientId: String = ChatParams.urlChatNameSpace!!,
        @Path("personId") personId : String,
        @Query("auth_token") visitorToken: String
    ) : Call<NetworkResultPersonPreview>

}