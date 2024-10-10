package com.ai.aishotclientkotlin.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.ai.aishotclientkotlin.data.dao.DeviceProfileDao
import com.ai.aishotclientkotlin.data.dao.entity.DeviceProfile
import com.ai.aishotclientkotlin.data.remote.DeviceProfileService
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class DeviceProfileRepository @Inject constructor(
    private val deviceProfileDao: DeviceProfileDao, // Room DAO
    private val deviceProfileService: DeviceProfileService // Retrofit API 服务
) {

    @WorkerThread
    fun loadDeviceProfiles(success: () -> Unit, error: () -> Unit): Flow<List<DeviceProfile>> = flow {
        var deviceProfiles = deviceProfileDao.getAllDeviceProfile()

        if (deviceProfiles.isEmpty()) {
            val response = deviceProfileService.fetchDeviceProfiles()
            response.suspendOnSuccess {
                deviceProfiles = data
                deviceProfileDao.insertDeviceProfiles(data)
                emit(deviceProfiles)
                success()
            }.onError {
                error()
            }.onException {
                error()
            }
        } else {
            emit(deviceProfiles)
        }
    }.flowOn(Dispatchers.IO)

    // 创建新评论
    @WorkerThread
    fun createDeviceProfile(deviceProfile: DeviceProfile, success: () -> Unit, error: () -> Unit): Flow<Result<DeviceProfile>> = flow {
       try {
           val response = deviceProfileService.createDeviceProfile(deviceProfile)
           response.suspendOnSuccess {
               deviceProfileDao.insertDeviceProfiles(listOf(data)) // 将新评论插入数据库
               Log.e("HTTP","send the deviceProfile : ${deviceProfile}")
               success()
               emit(Result.success(data))
           }
       }catch (e:Exception){
           error()
           emit(Result.failure<DeviceProfile>(e))
       }
    }.onCompletion {  success() }.flowOn(Dispatchers.IO)

    // 获取特定评论
    @WorkerThread
    fun loadDeviceProfile(id: Long, success: () -> Unit, error: () -> Unit): Flow<DeviceProfile> = flow {
        val deviceProfile = deviceProfileDao.getDeviceProfileById(id) // 从数据库获取
        if (deviceProfile == null) {
            val response = deviceProfileService.fetchDeviceProfile(id)
            response.suspendOnSuccess {
                emit(data)
                success()
            }.onError {
                error()
            }.onException {
                error()
            }
        } else {
            emit(deviceProfile)
        }
    }.flowOn(Dispatchers.IO)

    // 更新特定评论
    @WorkerThread
    fun updateDeviceProfile(id: Long, deviceProfile: DeviceProfile, success: () -> Unit, error: () -> Unit): Flow<Result<DeviceProfile>> = flow {
        try {
            val response = deviceProfileService.updateDeviceProfile(id, deviceProfile)
            response.suspendOnSuccess {
                deviceProfileDao.updateDeviceProfile(data) // Update the deviceProfile in the database
                emit(Result.success(data)) // Emit the updated deviceProfile wrapped in Result.success
                success() // Call success callback
            }
        } catch (e: Exception) {
            emit(Result.failure<DeviceProfile>(e)) // Emit the exception wrapped in Result.failure
            error() // Call error callback
        }
    }.flowOn(Dispatchers.IO)

    // 删除特定评论
    @WorkerThread
    fun deleteDeviceProfile(id: Long): Flow<Result<Unit>> = flow {
        try {
            // 调用服务删除评论
            deviceProfileService.deleteDeviceProfile(id)
            // 从数据库删除评论
            deviceProfileDao.deleteDeviceProfile(id)
            // 返回成功结果
            emit(Result.success(Unit))
        } catch (e: Exception) {
            // 捕获异常并返回失败结果
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
