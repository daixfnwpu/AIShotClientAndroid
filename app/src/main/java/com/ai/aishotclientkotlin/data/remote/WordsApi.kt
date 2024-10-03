package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.domain.model.login.AccessTokenModel
import com.ai.aishotclientkotlin.domain.model.login.CrudModel
import com.ai.aishotclientkotlin.domain.model.login.LoginModel
import com.ai.aishotclientkotlin.domain.model.login.KelimelerModel
import com.ai.aishotclientkotlin.domain.model.login.LanguagesModel
import com.ai.aishotclientkotlin.domain.model.login.RefreshTokenModel
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface WordsApi {

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