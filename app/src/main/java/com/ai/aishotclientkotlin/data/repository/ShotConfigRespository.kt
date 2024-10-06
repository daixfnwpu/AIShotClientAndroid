package com.ai.aishotclientkotlin.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.ai.aishotclientkotlin.data.dao.MovieDao
import com.ai.aishotclientkotlin.data.dao.ShopDao
import com.ai.aishotclientkotlin.data.dao.ShotConfigDao
import com.ai.aishotclientkotlin.data.remote.ShotConfigService
import com.ai.aishotclientkotlin.data.remote.TheDiscoverService
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.ai.aishotclientkotlin.domain.model.bi.network.AddShotConfigResponse
import com.ai.aishotclientkotlin.domain.model.bi.network.UpdateShotConfigResponse
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import timber.log.Timber
import javax.inject.Inject

class ShotConfigRespository @Inject constructor(
    private val shotConfigService: ShotConfigService,
    private val shotConfigDao: ShotConfigDao

) : Repository {

    init {
        Timber.d("Injection ShotConfigRespository")
    }

    @WorkerThread
    fun loadShotConfigs(success: () -> Unit, error: () -> Unit) = flow {
        var configs = shotConfigDao.getShotAllConfig()
        if (configs.isEmpty()) {
            val response = shotConfigService.fetchShotConfigs()
            response.suspendOnSuccess {
                configs = data.results
                //   configs.forEach { it.page = page }
                configs.forEach {
                    shotConfigDao.insertShotConfig(it)
                }

                emit(configs)
                success()
            }.onError {
                error()
            }.onException { error() }
        } else {
            emit(configs)
        }
    }.onCompletion {  }.flowOn(Dispatchers.IO)

    suspend fun addConfig(shotConfig: ShotConfig): Boolean {
        try {
           var id =  shotConfigDao.insertShotConfig(shotConfig)
            shotConfig.configUI_id =id
        }catch (e: Exception) { Log.e("DAO","insertShotConfig error:${e.toString()}")}
        return when (val response = shotConfigService.addShotConfig(shotConfig)) {
            is ApiResponse.Success -> {
                true // 成功时返回 true
            }
            is ApiResponse.Failure -> {
                // 处理失败情况，可以记录日志或抛出异常
                Log.e("AddConfigError", "Failed to add config: ${response}")
                false // 失败时返回 false
            }
        }
    }

    suspend fun updateConfig(shotConfig: ShotConfig): Boolean {

        try {
            shotConfigDao.updateShotConfig(shotConfig)
        }catch (e: Exception) { Log.e("DAO","insertShotConfig error:${e.toString()}")}
        return when (val response =  shotConfigService.updateShotConfig(shotConfig.configUI_id,shotConfig)) {
            is ApiResponse.Success -> {
                true // 成功时返回 true
            }
            is ApiResponse.Failure -> {
                // 处理失败情况，可以记录日志或抛出异常
                Log.e("updateConfig", "Failed to add config: ${response}")
                false // 失败时返回 false
            }
        }

    }

    suspend fun deleteConfig(id: Long) : Boolean {
        try {
            val config = shotConfigDao.getConfigById(id)
            if (config != null) {
                shotConfigDao.deleteConfig(id)
            }
        }catch (e: Exception) { Log.e("DAO","deleteConfig error:${e.toString()}")}
        return when (val response =  shotConfigService.deleteConfig(id)) {
            is ApiResponse.Success -> {
                true // 成功时返回 true
            }
            is ApiResponse.Failure -> {
                // 处理失败情况，可以记录日志或抛出异常
                Log.e("deleteConfig", "Failed to add config: ${response}")
                false // 失败时返回 false
            }
        }
    }

}