package com.ai.aishotclientkotlin.data.dao.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Long,
    val title: String,
    val imageUrl: String?,
    val price: Double,
    val description: String,
    val rating: Double,
    val isOnSale: Boolean,
    val salePrice: Double?,
    val salesCount: Int,
    val isFavorited: Boolean = false // 收藏状态
)