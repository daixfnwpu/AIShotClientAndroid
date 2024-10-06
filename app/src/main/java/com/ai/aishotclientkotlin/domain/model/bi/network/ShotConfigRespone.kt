package com.ai.aishotclientkotlin.domain.model.bi.network

import androidx.compose.runtime.Immutable
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.google.gson.annotations.SerializedName


@Immutable
data class ShotConfigRespone(
    @SerializedName("results")
    val results: List<ShotConfig>,
    @SerializedName("total_results")
    val total_results: Int,
) : NetworkResponseModel


data class AddShotConfigResponse(
    val id: Int,  // 新创建配置的 ID
    val message: String // 可选的操作成功消息
) : NetworkResponseModel

data class UpdateShotConfigResponse(
    val success: Boolean, // 更新是否成功
    val message: String // 可选的操作成功消息
) : NetworkResponseModel
data class DeleteShotConfigResponse(
    val success: Boolean, // 更新是否成功
    val message: String // 可选的操作成功消息
) : NetworkResponseModel