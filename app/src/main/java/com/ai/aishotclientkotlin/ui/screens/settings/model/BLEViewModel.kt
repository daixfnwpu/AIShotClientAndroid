package com.ai.aishotclientkotlin.ui.screens.settings.model

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.ble.BLEManager
import com.ai.aishotclientkotlin.data.ble.Characteristic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class BLEViewModel(application: Application) : AndroidViewModel(application) {


  //  private val bleManager: BLEManager = BLEManager(application.applicationContext)

    // 用于存储扫描到的设备列表
    private val _scandevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scandevices: StateFlow<List<BluetoothDevice>> = _scandevices


    // 用于存储扫描到的设备列表
    private val _connectdevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val connectdevices: StateFlow<List<BluetoothDevice>> = _connectdevices
   // private val _bleState = MutableStateFlow("Disconnected")
   /* val bleState: StateFlow<String> = MutableStateFlow(
       if (_scandevices.value.isNotEmpty()) ("Connected") else ("Disconnected")
    )*/
    val bleState: StateFlow<String> = _connectdevices
        .map { devices ->
            if (devices.isNotEmpty()) {
                "Connected"
            } else {
                "Disconnected"
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Disconnected")

    // 保存所有特征的状态
    private val _characteristics = MutableStateFlow<Map<Characteristic, String>>(
        Characteristic.entries.associateWith { "" }
    )
    val characteristics: StateFlow<Map<Characteristic, String>> = _characteristics

    // 保存写操作的结果状态
    private val _writeResults = MutableStateFlow<Map<Characteristic, Boolean>>(
        Characteristic.entries.associateWith { false }
    )
    val writeResults: StateFlow<Map<Characteristic, Boolean>> = _writeResults

    var stateChangeCallback: (BluetoothDevice,Int) -> Unit = { device,newState ->
        Timber.tag("BLE").e(newState.toString())
        // _bleState.value = newState
        getConnectedDevices()
        //   BLEManager.reconnectLastDevice()
    }

    var characteristicReadCalback : (Characteristic,ByteArray) -> Unit = { characteristic, value ->
        _characteristics.value = _characteristics.value.toMutableMap().apply {
            this[characteristic] = value.toString(Charsets.UTF_8)
        }
    }

    var characteristicWriteCallback :(Characteristic,Boolean) -> Unit =
        {characteristic, success ->
            _writeResults.value = _writeResults.value.toMutableMap().apply {
                this[characteristic] = success
            }
        }
    var notificationReceivedCallback :(Characteristic,ByteArray) -> Unit = {characteristic, value ->
        _characteristics.value = _characteristics.value.toMutableMap().apply {
            this[characteristic] = value.toString(Charsets.UTF_8)
        }
    }
    init {
        // 设置 BLE 事件的回调

        BLEManager.onConnectionStateChanged.add(stateChangeCallback)

        BLEManager.onCharacteristicRead .add(characteristicReadCalback)

        BLEManager.onCharacteristicWrite.add(characteristicWriteCallback)

        BLEManager.onNotificationReceived .add(notificationReceivedCallback)
    }
    // 启动扫描
    @SuppressLint("MissingPermission")
    fun startScan() {
        viewModelScope.launch {
            val devicesSet = _scandevices.value.toMutableSet() // 使用集合来避免重复
            BLEManager.startScan { device ->
                // 当扫描到设备时，更新设备列表
               // device.name?.toString()?.let { Log.e("Ble", it) }
                if (device !in devicesSet) { // 仅当设备不在集合中时添加
                //    device.name?.toString()?.let { Log.e("Ble", it) }
                    if(device.name != null && (device.name.startsWith("AISC") || device.name.startsWith("CATAPULT"))) {
                        devicesSet.add(device)
                        device.name?.toString()?.let { Timber.tag("Ble").e(it) }
                        _scandevices.value = devicesSet.toList() // 更新设备列表

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
        viewModelScope.launch {
           val listDevice =  BLEManager.getConnectedDevices()
            _connectdevices.value = listDevice

            Timber.tag("BLE").e("getConnectedDevices Number is : %s", listDevice.size.toString())
            onGetted(listDevice) // 连接成功后回调
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


    override fun onCleared() {
        super.onCleared()
        // 释放资源，取消订阅等
        BLEManager.onConnectionStateChanged.remove(stateChangeCallback)

        BLEManager.onCharacteristicRead .remove(characteristicReadCalback)

        BLEManager.onCharacteristicWrite.remove(characteristicWriteCallback)

        BLEManager.onNotificationReceived .remove(notificationReceivedCallback)
        Log.e("BLE","ViewModel 销毁了，资源已清理")
    }


}
