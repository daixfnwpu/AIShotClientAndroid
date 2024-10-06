package com.ai.aishotclientkotlin.data.remote


import com.ai.aishotclientkotlin.domain.model.bi.network.KeywordListResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.ReviewListResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.ShopListResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.VideoListResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ShopService {
    /**
     * [Tv Shops](https://developers.themoviedb.org/3/shop/get-shop-keywords)
     *
     * Get the keywords that have been added to a TV show.
     *
     * @param [id] Specify the id of shop keywords.
     *
     * @return [VideoListResponse] response
     */
    @GET("/shop/{shop_id}/keywords")
    suspend fun fetchKeywords(@Path("shop_id") id: Long): ApiResponse<KeywordListResponse>

    /**
     * [Tv Shops](https://developers.themoviedb.org/3/shop/get-shop-videos)
     *
     * Get the videos that have been added to a TV show.
     *
     * @param [id] Specify the id of shop id.
     *
     * @return [VideoListResponse] response
     */
    @GET("/shop/{shop_id}/videos")
    suspend fun fetchVideos(@Path("shop_id") id: Long): ApiResponse<VideoListResponse>

    @GET("/shop/{shop_id}/videos")
    suspend fun fetchShops(): ApiResponse<ShopListResponse>
    /**
     * [Tv Reviews](https://developers.themoviedb.org/3/shop/get-shop-reviews)
     *
     * Get the reviews for a TV show.
     *
     * @param [id] Specify the id of shop id.
     *
     * @return [ReviewListResponse] response
     */
    @GET("/shop/{shop_id}/reviews")
    suspend fun fetchReviews(@Path("shop_id") id: Long): ApiResponse<ReviewListResponse>
}