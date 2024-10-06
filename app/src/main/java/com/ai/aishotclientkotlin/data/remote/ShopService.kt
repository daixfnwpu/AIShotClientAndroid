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
     * [Tv Shops](https://developers.themoviedb.org/3/tv/get-tv-keywords)
     *
     * Get the keywords that have been added to a TV show.
     *
     * @param [id] Specify the id of tv keywords.
     *
     * @return [VideoListResponse] response
     */
    @GET("/3/tv/{tv_id}/keywords")
    suspend fun fetchKeywords(@Path("tv_id") id: Long): ApiResponse<KeywordListResponse>

    /**
     * [Tv Shops](https://developers.themoviedb.org/3/tv/get-tv-videos)
     *
     * Get the videos that have been added to a TV show.
     *
     * @param [id] Specify the id of tv id.
     *
     * @return [VideoListResponse] response
     */
    @GET("/3/tv/{tv_id}/videos")
    suspend fun fetchShops(@Path("tv_id") id: Long): ApiResponse<ShopListResponse>

    /**
     * [Tv Reviews](https://developers.themoviedb.org/3/tv/get-tv-reviews)
     *
     * Get the reviews for a TV show.
     *
     * @param [id] Specify the id of tv id.
     *
     * @return [ReviewListResponse] response
     */
    @GET("/3/tv/{tv_id}/reviews")
    suspend fun fetchReviews(@Path("tv_id") id: Long): ApiResponse<ReviewListResponse>
}