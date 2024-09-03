package com.ai.aishotclientkotlin.domain.model.login


import com.google.gson.annotations.SerializedName

data class EklenmemisDillerModel(
    @SerializedName("DillerJSON")
    val dillerJSON: List<DillerJSON>,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int,
    @SerializedName("useraktifdil")
    val useraktifdil: Any
) {
    data class DillerJSON(
        @SerializedName("DilAd")
        val dilAd: String,
        @SerializedName("Dilid")
        val Dilid: Any,
        @SerializedName("Icon")
        val icon: String
    )
}