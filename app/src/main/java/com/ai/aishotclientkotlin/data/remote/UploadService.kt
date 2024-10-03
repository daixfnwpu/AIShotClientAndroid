package com.ai.aishotclientkotlin.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {
    @Multipart
    @POST("upload/")
    fun uploadFiles(
        @Part images: List<MultipartBody.Part>?,
        @Part video: MultipartBody.Part?,
        @Part("description") description: RequestBody?
    ): Call<ResponseBody>
}
