package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.domain.model.bi.bean.Review
import com.ai.aishotclientkotlin.domain.model.bi.network.KeywordListResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewService {
    // 获取所有评论
    @GET("/reviews/")
    suspend fun fetchReviews(): ApiResponse<List<Review>>

    // 创建新评论
    @POST("/reviews/")
    suspend fun createReview(@Body review: Review): ApiResponse<Review>

    // 获取特定评论
    @GET("/reviews/{id}/")
    suspend fun fetchReview(@Path("id") id: Long): ApiResponse<Review>

    // 更新特定评论
    @PUT("/reviews/{id}/")
    suspend fun updateReview(@Path("id") id: Long, @Body review: Review): ApiResponse<Review>

    // 删除特定评论
    @DELETE("/reviews/{id}/")
    suspend fun deleteReview(@Path("id") id: Long): ApiResponse<Unit>

}