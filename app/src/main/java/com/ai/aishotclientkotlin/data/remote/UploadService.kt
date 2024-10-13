package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.domain.model.login.UserAvatarResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UploadService {
    @Multipart
    @POST("upload/")
    fun uploadFiles(
        @Part images: List<MultipartBody.Part>?,
        @Part video: MultipartBody.Part?,
        @Part("description") description: RequestBody?
    ): Call<ResponseBody>

    @Multipart
    @PUT("api/upload-avatar/")
    fun uploadAvatar(
        @Part  avatar : MultipartBody.Part
    ): Call<ResponseBody>


    @GET("api/user/{id}/avatar/")
    fun getUserAvatar(@Path("id") userId: Int):Call<UserAvatarResponse>


}
