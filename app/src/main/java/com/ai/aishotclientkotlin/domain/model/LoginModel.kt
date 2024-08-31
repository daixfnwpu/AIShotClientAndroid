package com.ai.aishotclientkotlin.domain.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("LoginJSON")
    val loginJSON: List<LoginJSON>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
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