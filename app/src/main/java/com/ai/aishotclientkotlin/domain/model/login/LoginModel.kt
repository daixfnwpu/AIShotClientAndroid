package com.ai.aishotclientkotlin.domain.model.login

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("LoginJSON")
    val loginJSON: List<LoginJSON>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int,
    @SerializedName("access")
    val access : String,
    @SerializedName("refresh")
    val refresh : String,
    @SerializedName("userId")
    val userId : String
) {
    data class LoginJSON(
        @SerializedName("Pst_PhoneNum")
        val pstPhoneNum: String,
        @SerializedName("Pst_UserID")
        val pstUserID: String,
        @SerializedName("Pst_Password")
        val pstPassword: String
    )
}