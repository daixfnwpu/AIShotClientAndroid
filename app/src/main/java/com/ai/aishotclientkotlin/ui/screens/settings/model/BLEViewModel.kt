package com.ai.aishotclientkotlin.ui.screens.settings.model

import android.app.Application
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.shotclient.BLEManager
import kotlinx.coroutines.launch

class BLEViewModel(application: Application) : AndroidViewModel(application) {

    private val bleManager: BLEManager = BLEManager(application.applicationContext)

    // 启动扫描
    fun startScan() {
        viewModelScope.launch {
            bleManager.startScan()
        }
    }

    // 停止扫描
    fun stopScan() {
        viewModelScope.launch {
            bleManager.stopScan()
        }
    }

    // 连接到设备
    fun connectToDevice(deviceAddress: String) {
        viewModelScope.launch {
            bleManager.connectToDevice(deviceAddress)
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
