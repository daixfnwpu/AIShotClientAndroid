package com.ai.aishotclientkotlin.data.dao.converters
import androidx.room.TypeConverter
import com.ai.aishotclientkotlin.data.dao.entity.Review
import com.ai.aishotclientkotlin.data.dao.entity.Video
import com.ai.aishotclientkotlin.domain.model.bi.bean.Keyword
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShopListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromKeywordList(keywords: List<Keyword>?): String? {
        return gson.toJson(keywords)
    }

    @TypeConverter
    fun toKeywordList(data: String?): List<Keyword>? {
        val listType = object : TypeToken<List<Keyword>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromVideoList(videos: List<Video>?): String? {
        return gson.toJson(videos)
    }

    @TypeConverter
    fun toVideoList(data: String?): List<Video>? {
        val listType = object : TypeToken<List<Video>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromReviewList(reviews: List<Review>?): String? {
        return gson.toJson(reviews)
    }

    @TypeConverter
    fun toReviewList(data: String?): List<Review>? {
        val listType = object : TypeToken<List<Review>>() {}.type
        return gson.fromJson(data, listType)
    }
}
