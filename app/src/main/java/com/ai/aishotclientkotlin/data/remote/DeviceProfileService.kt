package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.data.dao.entity.DeviceProfile
import com.ai.aishotclientkotlin.domain.model.bi.network.KeywordListResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DeviceProfileService {
    // 获取所有评论
    @GET("/api/device/profile/")
    suspend fun fetchDeviceProfiles(): ApiResponse<List<DeviceProfile>>

    // 创建新评论
    @POST("/api/device/profile/")
    suspend fun createDeviceProfile(@Body deviceProfile: DeviceProfile): ApiResponse<DeviceProfile>

    // 获取特定评论
    @GET("/api/device/profile/{id}/")
    suspend fun fetchDeviceProfile(@Path("id") id: Long): ApiResponse<DeviceProfile>

    // 更新特定评论
    @PUT("/api/device/profile/{id}/")
    suspend fun updateDeviceProfile(@Path("id") id: Long, @Body deviceProfile: DeviceProfile): ApiResponse<DeviceProfile>

    // 删除特定评论
    @DELETE("/api/device/profile/{id}/")
    suspend fun deleteDeviceProfile(@Path("id") id: Long): ApiResponse<Unit>

}