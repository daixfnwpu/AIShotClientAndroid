package com.ai.aishotclientkotlin.ui.screens.settings.model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DeviceInfoViewModel @Inject constructor() : ViewModel() {

    // 私有的设备状态，初始为空列表
    private val _deviceState = MutableStateFlow(listOf<DeviceProfile>())
    val deviceState: StateFlow<List<DeviceProfile>> = _deviceState

    // 添加新的设备
    fun addDevice(device: DeviceProfile) {
        _deviceState.value = _deviceState.value + device
    }

    // 根据索引更新某个设备的信息
    fun updateDevice(index: Int, updatedDevice: DeviceProfile) {
        _deviceState.value = _deviceState.value.toMutableList().also {
            it[index] = updatedDevice
        }
    }

    // 更新设备的 Wi-Fi 信息
    fun updateDeviceWiFi(index: Int, wifiAccount: String, wifiPassword: String) {
        _deviceState.value[index].updateWiFiCredentials(wifiAccount, wifiPassword)
        // 更新状态流
        _deviceState.value = _deviceState.value.toList()
    }

    // 更新设备的 BLE 连接状态
    fun updateBLEConnection(index: Int, isConnected: Boolean) {
        _deviceState.value[index].bleConnection = isConnected
        // 更新状态流
        _deviceState.value = _deviceState.value.toList()
    }

    // 获取设备电量
    fun getDeviceBatteryLevel(index: Int): Int {
        return _deviceState.value[index].getBatteryLevel()
    }
}

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

    // 方法：获取电量百分比
    fun getBatteryLevel(): Int {
        return batteryLevel
    }
}
