package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.ai.aishotclientkotlin.domain.model.bi.network.AddShotConfigResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.DeleteShotConfigResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.DiscoverMovieResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.ShotConfigRespone
import com.ai.aishotclientkotlin.domain.model.bi.network.UpdateShotConfigResponse
import com.ai.aishotclientkotlin.domain.model.login.CrudModel
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ShotConfigService {

    @GET("/api/shotconfigs/")
    suspend fun fetchShotConfigs( @Query("isalreadyDown") isalreadyDown: Int? = null): ApiResponse<ShotConfigRespone>

    @GET("/api/shotconfig/{id}/")
    suspend fun fetchShotConfig(@Path("id") id : Long): ApiResponse<ShotConfig>


    @POST("/api/shotconfig/")
    suspend fun addShotConfig(@Body shotConfig: ShotConfig): ApiResponse<AddShotConfigResponse>

    @PUT("/api/shotconfig/{id}/")
    suspend fun updateShotConfig(@Path("id") id: Long,@Body shotConfig: ShotConfig): ApiResponse<UpdateShotConfigResponse>

    @DELETE("/api/shotconfig/{id}/")
    suspend fun deleteConfig(@Path("id") id: Long): ApiResponse<DeleteShotConfigResponse>

}
