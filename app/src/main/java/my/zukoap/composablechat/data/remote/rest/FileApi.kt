package my.zukoap.composablechat.data.remote.rest

import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.data.ApiParams
import my.zukoap.composablechat.domain.entity.file.NetworkBodyStructureUploadFile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface FileApi {

    @Headers("Content-Type: application/json")
    @POST("webchat/{clientId}/upload-file")
    fun uploadFile(
        @Path("clientId") clientId: String = ChatParams.urlChatNameSpace!!,
        @Query("auth_token") visitorToken: String,
        @Body networkBody: NetworkBodyStructureUploadFile
    ): Call<String>

    @Multipart
    @POST("webchat/{clientId}/upload-file")
    fun uploadFile(
        @Path("clientId") clientId: String = ChatParams.urlChatNameSpace!!,
        @Query("auth_token") visitorToken: String,
        @Part(ApiParams.FILE_NAME) fileName: RequestBody,
        @Part(ApiParams.UUID) uuid: RequestBody,
        @Part fileB64: MultipartBody.Part
    ): Call<String>

}