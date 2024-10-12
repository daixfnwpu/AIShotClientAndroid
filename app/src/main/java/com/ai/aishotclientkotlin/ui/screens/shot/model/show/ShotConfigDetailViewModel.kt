package com.ai.aishotclientkotlin.ui.screens.shot.model.show

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.repository.ShotConfigRespository
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/*            thinofrubber (皮筋厚度)
4.     crossofrubber (皮筋的夹角):
5.     initlengthofrubber (皮筋的初始长度);
6.     widthofrubber (皮筋的宽度);

humidity (空气湿度);
Cd （圆球的阻力系数，常数）；
rho(空气密度)*/

@HiltViewModel
class ShotConfigDetailViewModel @Inject constructor(val shotConfigRespository: ShotConfigRespository) :
    ViewModel() {

    private val _configDetail = MutableStateFlow<ShotConfig?>(null)
    val configDetail: StateFlow<ShotConfig?> = _configDetail

    private val _isLoading = mutableStateOf(true) // 初始状态为加载中
    val isLoading: State<Boolean> = _isLoading


    fun loadItemDetails(id: Long,onComplete: () ->Unit) {
        viewModelScope.launch {
            shotConfigRespository.loadShotConfigById(id, success = {
                Log.e("HTTP", "loadShotConfigById Success")
            }, error = {
                Log.e("HTTP", "loadShotConfigById Error")
            }).collectLatest {

                _configDetail.value = it
                _isLoading.value = false
                onComplete()
            } // 根据 itemId 获取详细信息
        }
    }

    fun createNewConfig() {
        // TODO("create the new ShotConfig")
        val newConfig = ShotConfig()
        _configDetail.value = newConfig // 将新创建的配置设置为当前配置
    }


    // 更新某个配置
    fun updateConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            configDetail.value?.let { shotConfigRespository.updateConfig(it) }
        }
    }

    fun saveConfig() {
        viewModelScope.launch {
            _configDetail.value?.let { shotConfigRespository.addConfig(it) }
        }
    }


}
