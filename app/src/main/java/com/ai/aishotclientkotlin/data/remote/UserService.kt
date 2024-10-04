package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.domain.model.login.AccessTokenModel
import com.ai.aishotclientkotlin.domain.model.login.CrudModel
import com.ai.aishotclientkotlin.domain.model.login.LoginModel
import com.ai.aishotclientkotlin.domain.model.login.KelimelerModel
import com.ai.aishotclientkotlin.domain.model.login.LanguagesModel
import com.ai.aishotclientkotlin.domain.model.login.RefreshTokenModel
import com.ai.aishotclientkotlin.domain.model.login.UserAvatarResponse
import com.skydoves.sandwich.ApiResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

interface UserService {

    @POST
    @FormUrlEncoded
    suspend fun logInUser(
        @Url url: String?,
        @Field("Pst_App_ID") AppCode: String,
        @Field("Pst_PhoneNum") PhoneNum: String,
        @Field("Pst_Password") Password: String
    ): LoginModel

    @POST
    @FormUrlEncoded
    suspend fun signInUser(
        @Url url: String?,
        @Field("Pst_App_ID") AppCode: String,
        @Field("Pst_PhoneNum") PhoneNum: String,
        @Field("Pst_Password") Sifre: String
    ): CrudModel

    @Multipart
    @POST("api/upload-avatar/")
    suspend fun uploadAvatar(
        @Part  avatar : MultipartBody.Part
    ): Call<ResponseBody>


    @GET("api/user/{id}/avatar/")
    fun getUserAvatar(@Path("id") userId: Int):Call<UserAvatarResponse>


    @POST
    @FormUrlEncoded
    suspend fun listAllLanguages(
        @Url url: String?,
        @Field("Pst_App_ID") AppCode: String,
        @Field("Pst_User_ID") Pst_User_ID: String)
            : LanguagesModel

    @POST
    @FormUrlEncoded
    suspend fun listAllWords(
        @Url url: String?,
        @Field("Pst_App_ID") AppCode: String,
        @Field("Pst_Kullanici_Dil_ID") Pst_Dil_ID: String,
        @Field("Pst_User_ID") Pst_User_ID: String
    ): KelimelerModel

    @POST
    fun refreshToken(@Url url: String,@Body refreshToken: RefreshTokenModel): AccessTokenModel

}