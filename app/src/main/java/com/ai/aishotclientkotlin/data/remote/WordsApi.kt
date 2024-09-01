package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.domain.model.CrudModel
import com.ai.aishotclientkotlin.domain.model.LoginModel
import com.ai.aishotclientkotlin.domain.model.KelimelerModel
import com.ai.aishotclientkotlin.domain.model.LanguagesModel
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
        @Field("Pst_Password") Sifre: String
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

}