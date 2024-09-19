package com.ai.aishotclientkotlin.ui.screens.settings.model

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.shotclient.BLEManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class BLEViewModel(application: Application) : AndroidViewModel(application) {


  //  private val bleManager: BLEManager = BLEManager(application.applicationContext)

    // 用于存储扫描到的设备列表
    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices


    // 用于存储扫描到的设备列表
    private val _connectdevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val connectdevices: StateFlow<List<BluetoothDevice>> = _connectdevices

    // 启动扫描
    @SuppressLint("MissingPermission")
    fun startScan() {
        viewModelScope.launch {
            val devicesSet = _devices.value.toMutableSet() // 使用集合来避免重复
            BLEManager.startScan { device ->
                // 当扫描到设备时，更新设备列表
               // device.name?.toString()?.let { Log.e("Ble", it) }
                if (device !in devicesSet) { // 仅当设备不在集合中时添加
                //    device.name?.toString()?.let { Log.e("Ble", it) }
                    if(device.name != null && (device.name.startsWith("AISC") || device.name.startsWith("CATAPULT"))) {
                        devicesSet.add(device)
                        device.name?.toString()?.let { Log.e("Ble", it) }
                        _devices.value = devicesSet.toList() // 更新设备列表
                        BLEManager.reconnectLastDevice()
                    }
                }
            }
        }
    }

    // 停止扫描
    fun stopScan() {
        viewModelScope.launch {
            BLEManager.stopScan()
        }
    }

    // 连接到设备
    fun connectToDevice(device: BluetoothDevice, onConnected: () -> Unit) {
        viewModelScope.launch {
            BLEManager.connectToDevice(device.address)
            onConnected() // 连接成功后回调
        }
    }
    fun getConnectedDevices(onGetted:(List<BluetoothDevice>) -> Unit = {})  {
      //  viewModelScope.launch {
           val listDevice =  BLEManager.getConnectedDevices()
            _connectdevices.value = listDevice
            Timber.tag("BLE").e( "getConnectedDevices Number is : %s",listDevice.size.toString(),)
            onGetted(listDevice) // 连接成功后回调
      //  }
    }
    // 断开设备连接
    fun disconnect() {
        viewModelScope.launch {
            BLEManager.disconnect()
        }
    }
    fun readData(characteristic: BluetoothGattCharacteristic) {

    }
    // 写入数据
    fun writeData(characteristic: BluetoothGattCharacteristic, data: ByteArray) {
        viewModelScope.launch {
            BLEManager.writeCharacteristic(characteristic, data)
        }
    }


    enum class Charatic {

    }
}
