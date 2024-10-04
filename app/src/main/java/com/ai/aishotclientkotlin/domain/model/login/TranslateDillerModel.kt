package com.ai.aishotclientkotlin.domain.model.login


import com.google.gson.annotations.SerializedName

data class TranslateDillerModel(
    @SerializedName("data")
    val `data`: Data
) {
    data class Data(
        @SerializedName("languages")
        val languages: List<Language>
    ) {
        data class Language(
            @SerializedName("language")
            val language: String,
            @SerializedName("name")
            val name: String
        )
    }
}
data class UserAvatarResponse(
    val avatar: String // 头像的 URL
)