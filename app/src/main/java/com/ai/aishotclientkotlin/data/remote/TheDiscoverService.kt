package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.domain.model.bi.network.DiscoverMovieResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.DiscoverShopResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TheDiscoverService {
    /**
     * [Movie Discover](https://developers.themoviedb.org/3/discover/movie-discover)
     *
     *  Discover movies by different types of data like average rating, number of votes, genres and certifications.
     *  You can get a valid list of certifications from the  method.
     *
     *  @param [page] Specify the page of results to query.
     *
     *  @return [DiscoverMovieResponse] response
     */
    @GET("/discover/movie?language=en&sort_by=popularity.desc")
    suspend fun fetchDiscoverMovie(@Query("page") page: Int): ApiResponse<DiscoverMovieResponse>

    /**
     * [Shop Discover](https://developers.themoviedb.org/3/discover/tv-discover)
     *
     *  Discover TV shows by different types of data like average rating, number of votes, genres, the network they aired on and air dates.
     *
     *  @param [page] Specify the page of results to query.
     *
     *  @return [DiscoverShopResponse] response
     */
    @GET("/discover/shop?language=en&sort_by=popularity.desc")
    suspend fun fetchDiscoverShop(@Query("page") page: Int): ApiResponse<DiscoverShopResponse>
}
