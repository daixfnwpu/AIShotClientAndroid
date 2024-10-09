package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.domain.model.bi.network.DiscoverMovieResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TheDiscoverService {

    @GET("/discover/movie?language=en&sort_by=popularity.desc")
    suspend fun fetchDiscoverMovie(@Query("page") page: Int): ApiResponse<DiscoverMovieResponse>

}
