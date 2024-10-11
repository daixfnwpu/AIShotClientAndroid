package com.ai.aishotclientkotlin.ui.screens.settings.model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.dao.entity.DeviceProfile
import com.ai.aishotclientkotlin.data.repository.DeviceProfileRepository
import com.ai.aishotclientkotlin.domain.model.bi.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceInfoViewModel @Inject constructor(private val repository: DeviceProfileRepository) : ViewModel() {


    private val _profileLoadingState: MutableState<NetworkState> = mutableStateOf(NetworkState.IDLE)
    val profileLoadingState: State<NetworkState> get() = _profileLoadingState
    // 私有的设备状态，初始为空列表
    private val _deviceProfile = MutableStateFlow(listOf<DeviceProfile>())
    val deviceProfile: StateFlow<List<DeviceProfile>> = _deviceProfile

    // 添加新的设备
    fun addDevice(device: DeviceProfile) {
        _deviceProfile.value += device
    }


    init {
        viewModelScope.launch {
            try {
               repository.loadDeviceProfiles( success = {
                   _profileLoadingState.value = NetworkState.SUCCESS
                   Log.e("loadDeviceProfiles","loadDeviceProfiles.loadDevices sucess")
                                                        },
                   error = {
                       _profileLoadingState.value = NetworkState.ERROR
                       Log.e("loadDeviceProfiles","loadDeviceProfiles.loadDevices error")}).
               collectLatest {

                   Log.e("loadDeviceProfiles","loadDeviceProfiles.loadDevices ${it}")
                   _deviceProfile.emit(it)
                }
            } catch (e: Exception) {
                // Handle errors (e.g., network failure)
                _profileLoadingState.value = NetworkState.ERROR
                _deviceProfile.value = emptyList()
            }
        }
    }
    // 根据索引更新某个设备的信息
    fun updateDevice(index: Int, updatedDevice: DeviceProfile) {
        _deviceProfile.value = _deviceProfile.value.toMutableList().also {
            it[index] = updatedDevice
        }
    }

    // 更新设备的 Wi-Fi 信息
    fun updateDeviceWiFi(index: Int, wifiAccount: String, wifiPassword: String) {
        _deviceProfile.value[index].wifi_account = wifiAccount
        _deviceProfile.value[index].wifi_password = wifiPassword
        // 更新状态流
        _deviceProfile.value = _deviceProfile.value.toList()
    }

    // 更新设备的 BLE 连接状态
    fun updateBLEConnection(index: Int, isConnected: Boolean) {
        _deviceProfile.value[index].ble_connection = isConnected
        // 更新状态流
        _deviceProfile.value = _deviceProfile.value.toList()
    }

    // 获取设备电量
    fun getDeviceBatteryLevel(index: Int): Int {
        return _deviceProfile.value[index].battery_level
    }
}

/*
data class DeviceProfile(
    var model: String = "铂金版",             // 型号，默认是 "铂金版"
    var bowGateDistance: Float = 0.04f,           // 弓门距离，默认 0.04m
    var headWidth: Float = 0.025f,                // 端头宽度，默认 0.025m
    var rubberThickness: Float = 0.0045f,         // 皮筋厚度，默认 0.0045m
    var initialRubberLength: Float = 0.22f,       // 皮筋初始化长度，默认 0.22m
    var rubberWidth: Float = 0.025f,              // 皮筋宽度，默认等于端头宽度
    var wifiAccount: String = "aishotclient",     // Wi-Fi 账号，默认值
    var wifiPassword: String = "aishotclient123", // Wi-Fi 密码，默认值
    var bleConnection: Boolean = false,           // BLE 连接状态，默认 false
    var batteryLevel: Int = 100                   // 电量，默认 100%
) {
    // 方法：更新设备型号
    fun updateModel(newModel: String) {
        model = newModel
    }

    // 方法：更新 Wi-Fi 账号和密码
    fun updateWiFiCredentials(account: String, password: String) {
        wifiAccount = account
        wifiPassword = password
    }

    // 方法：检查 BLE 连接状态
    fun checkBLEConnection(): Boolean {
        return bleConnection
    }
}
*/
