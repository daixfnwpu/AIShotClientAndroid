package com.ai.aishotclientkotlin.ui.screens.settings.model

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.shotclient.BLEManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BLEViewModel(application: Application) : AndroidViewModel(application) {


    private val bleManager: BLEManager = BLEManager(application.applicationContext)

    // 用于存储扫描到的设备列表
    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices

    // 启动扫描
    fun startScan() {
        viewModelScope.launch {
            val devicesSet = _devices.value.toMutableSet() // 使用集合来避免重复
            bleManager.startScan { device ->
                // 当扫描到设备时，更新设备列表

                if (device !in devicesSet) { // 仅当设备不在集合中时添加
                    if(device.name.startsWith("AISC") || device.name.startsWith("CATAPULT")) {
                        devicesSet.add(device)
                        _devices.value = devicesSet.toList() // 更新设备列表
                    }
                }
            }
        }
    }

    // 停止扫描
    fun stopScan() {
        viewModelScope.launch {
            bleManager.stopScan()
        }
    }

    // 连接到设备
    fun connectToDevice(device: BluetoothDevice, onConnected: () -> Unit) {
        viewModelScope.launch {
            bleManager.connectToDevice(device.address)
            onConnected() // 连接成功后回调
        }
    }

    // 断开设备连接
    fun disconnect() {
        viewModelScope.launch {
            bleManager.disconnect()
        }
    }

    // 写入数据
    fun writeData(characteristic: BluetoothGattCharacteristic, data: ByteArray) {
        viewModelScope.launch {
            bleManager.writeCharacteristic(characteristic, data)
        }
    }
}
