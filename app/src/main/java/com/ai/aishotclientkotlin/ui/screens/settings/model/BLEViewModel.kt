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
import com.ai.aishotclientkotlin.data.shotclient.BLEManager.bluetoothGatt
import com.ai.aishotclientkotlin.data.shotclient.Characteristic
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
    private val _bleState = MutableStateFlow("Disconnected")
    val bleState: StateFlow<String> = if (_devices.value.isNotEmpty()) MutableStateFlow("Connected") else MutableStateFlow("Disconnected")

    // 保存所有特征的状态
    private val _characteristics = MutableStateFlow<Map<Characteristic, Any?>>(
        Characteristic.values().associateWith { null }
    )
    val characteristics: StateFlow<Map<Characteristic, Any?>> = _characteristics

    // 保存写操作的结果状态
    private val _writeResults = MutableStateFlow<Map<Characteristic, Boolean>>(
        Characteristic.values().associateWith { false }
    )
    val writeResults: StateFlow<Map<Characteristic, Boolean>> = _writeResults

    init {
        // 设置 BLE 事件的回调
        BLEManager.onConnectionStateChanged = { newState ->
            Log.e("BLE",newState)
            _bleState.value = newState
        }

        BLEManager.onCharacteristicRead = { characteristic, value ->
            _characteristics.value = _characteristics.value.toMutableMap().apply {
                this[characteristic] = value
            }
        }

        BLEManager.onCharacteristicWrite = { characteristic, success ->
            _writeResults.value = _writeResults.value.toMutableMap().apply {
                this[characteristic] = success
            }
        }

        BLEManager.onNotificationReceived = { characteristic, value ->
            _characteristics.value = _characteristics.value.toMutableMap().apply {
                this[characteristic] = value
            }
        }
    }
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

    // 通过 BleManager 读特征值
    fun readDataFromCharacteristic(characteristic: Characteristic) {
        BLEManager.readCharacteristic(characteristic)
    }

    // 通过 BleManager 写特征值
    fun writeDataToCharacteristic(characteristic: Characteristic, data: String) {
        BLEManager.writeCharacteristic(characteristic, data.toByteArray(Charsets.UTF_8))
    }

    // 启用/禁用通知
    fun enableNotifications(characteristic: Characteristic, enable: Boolean) {
        BLEManager.enableNotifications(characteristic, enable)
    }



}
