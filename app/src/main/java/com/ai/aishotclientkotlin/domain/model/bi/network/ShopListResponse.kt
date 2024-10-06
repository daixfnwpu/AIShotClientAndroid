package com.ai.aishotclientkotlin.domain.model.bi.network

import androidx.compose.runtime.Immutable
import com.ai.aishotclientkotlin.data.dao.entity.Shop

@Immutable
data class ShopListResponse(
    val id: Int,
    val results: List<Shop>
) : NetworkResponseModel
