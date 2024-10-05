package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.domain.model.bi.entity.ShotConfig
import com.ai.aishotclientkotlin.domain.model.bi.network.AddShotConfigResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.DeleteShotConfigResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.DiscoverMovieResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.DiscoverShopResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.ShotConfigRespone
import com.ai.aishotclientkotlin.domain.model.bi.network.UpdateShotConfigResponse
import com.ai.aishotclientkotlin.domain.model.login.CrudModel
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ShotConfigService {

    @GET("/api/shotconfig/getall/")
    suspend fun fetchShotConfigs(): ApiResponse<ShotConfigRespone>

    @POST("/api/shotconfig/add/")
    suspend fun addShotConfig(@Body shotConfig: ShotConfig): ApiResponse<AddShotConfigResponse>

    @PUT("/api/shotconfig/update/")
    suspend fun updateShotConfig(@Body shotConfig: ShotConfig): ApiResponse<UpdateShotConfigResponse>

    @POST("/api/shotconfig/delete/{id}/")
    suspend fun deleteConfig(@Path("id") id: Int): ApiResponse<DeleteShotConfigResponse>
}
