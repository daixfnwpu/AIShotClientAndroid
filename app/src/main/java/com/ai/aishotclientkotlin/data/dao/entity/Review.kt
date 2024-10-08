package com.ai.aishotclientkotlin.data.dao.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "review") // 指定表名
data class Review(
    @PrimaryKey()
    @SerializedName("id") // 对应 JSON 中的 id 字段
    val id: Long? = null,
    @SerializedName("movie") // 对应 JSON 中的 author 字段
    val movieId: Long?,

    @SerializedName("author") // 对应 JSON 中的 author 字段
    val author: String,
    @SerializedName("content") // 对应 JSON 中的 content 字段
    val content: String,
    @SerializedName("user")
    val userId : Long?,

    @SerializedName("url") // 对应 JSON 中的 url 字段
    val url: String
)
