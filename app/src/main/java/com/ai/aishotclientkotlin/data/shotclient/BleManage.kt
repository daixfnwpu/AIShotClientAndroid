package com.ai.aishotclientkotlin.data.shotclient

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import com.ai.aishotclientkotlin.util.SpManager
import timber.log.Timber
import java.util.*
//!!! TODO : bluetoothGatt is not null ,表示已经连接成功？
/// !!! TODO: buletooth的服务器，必须要重启后，才能够再连接？！！可否支持不用线扫描就连接呢？
/// ！！！TODO： BluetoothGatt 发送和接收消息，或者是订阅消息；
/// !!! TODO: 测试scan ble的stop功能。

@SuppressLint("MissingPermission")
object BLEManager {

    val serviceUuid: UUID =
        UUID.fromString("0000181a-0000-1000-8000-00805f9b34fb") // Replace with actual service UUID
    val characteristicUuid: UUID =
        UUID.fromString("00002a6e-0000-1000-8000-00805f9b34fb") // Replace with actual characteristic UUID
    val descriptorUuid: UUID =
        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") // Replace with actual descriptor UUID


    private var appContext: Context? = null
    fun initialize(context: Context) {
        // 初始化相关操作
        Log.e("ble","initialize")
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        appContext = context
    }

    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private var bluetoothGatt: BluetoothGatt? = null
    // 定义 ScanSettings
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // 设置为高频扫描模式
        .build()

    // GATT回调
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Timber.tag("BLE").e("Connected to GATT server.")
                    gatt?.discoverServices() // 发现服务
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Timber.tag("BLE").e("Disconnected from GATT server.")
                bluetoothGatt?.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                subscribeToCharacteristic()

            } else {
                Timber.tag("BLE").e("onServicesDiscovered received: " + status)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic?.value
                Timber.tag("BLE").e("Characteristic read: " + (data?.toString(Charsets.UTF_8) ?:"没有值" ))
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.tag("BLE").e("Characteristic write successful")
            } else {
                Timber.tag("BLE").e("Characteristic write failed")
            }
        }
        //onCharacteristicChanged: Invoked when the BLE device sends a notification for the characteristic you’ve subscribed to. This is where you read the updated data.
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            val data = characteristic?.value

            Timber.tag("BLE").e("Received data: " + (data?.toString(Charsets.UTF_8) ?:"没有值" ))
        }
    }


    fun startScan(onDeviceFound: (BluetoothDevice) -> Unit) {

        bluetoothLeScanner?.startScan(null, scanSettings,object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)

                result?.device?.let {
                    onDeviceFound(it)
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                results?.forEach { result ->
                    result.device?.let {
                        onDeviceFound(it)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                // 扫描失败处理 // TODO: 提示，关闭蓝牙后，再打开，同时，注意查看shot设备是否打开？
            }
        })
    }

    fun stopScan() {
        bluetoothLeScanner?.stopScan(object : ScanCallback() {})
    }
    // 获取已连接的设备
    fun getConnectedDevices(): List<BluetoothDevice> {
        return bluetoothManager.getConnectedDevices(BluetoothProfile.GATT) // 获取 GATT 连接的设备
    }

    fun connectToDevice(deviceAddress: String) {
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        device?.let { Timber.tag("ble").e("Call connectToDevice %s,%s",device.address, it.uuids) }
        bluetoothGatt = device?.connectGatt(appContext, false,gattCallback )
    }
    private fun saveDeviceAddress(deviceAddress: String) {

        appContext?.let { SpManager(it).setSharedPreference(SpManager.Sp.BLE,deviceAddress) }
    }

    fun reconnectLastDevice() {
        Log.e("BLE","In The BLEManger reconnect the Device")
        val lastDeviceAddress =  appContext?.let { SpManager(it).getSharedPreference(SpManager.Sp.BLE,null) }                      //sharedPreferences?.getString("LAST_CONNECTED_DEVICE", null)
        Timber.tag("BLE").e("lastDeviceAddress%s", lastDeviceAddress)

        lastDeviceAddress?.let { connectToDevice(it) }
    }
    // 断开设备连接
    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        Timber.tag("BLE").e("Disconnected from device")
    }

    private fun subscribeToCharacteristic() {
        bluetoothGatt?.let { gatt ->

            Timber.tag("BLE").e("Services discovered")
            // 获取服务并处理特征
            val service = gatt?.getService(serviceUuid)
            val characteristic = service?.getCharacteristic(characteristicUuid)
            if (characteristic != null) {
                readCharacteristic(characteristic)
            }

            characteristic?.let {
                // Enable notifications
                gatt.setCharacteristicNotification(it, true)

                // Configure the descriptor for notifications
                val descriptor = it.getDescriptor(descriptorUuid)
                descriptor?.let { desc ->
                    // Check if ENABLE_NOTIFICATION_VALUE is deprecated
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Use updated methods or constants if available
                        desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    } else {
                        // Use legacy value if updated constants are not available
                        desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    }
                    gatt.writeDescriptor(desc)
                }
            }
        }
    }

    // 读取特征
    @SuppressLint("MissingPermission")
    private fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.readCharacteristic(characteristic)
        Timber.tag("BLE").e("Reading characteristic: " + characteristic.uuid)
    }

    // 写入特征
    @SuppressLint("MissingPermission")
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, data: ByteArray) {
        characteristic.value = data
        bluetoothGatt?.writeCharacteristic(characteristic)
        Timber.tag("BLE")
            .e("Writing characteristic: " + characteristic.uuid + ", data: " + data.joinToString(","))
    }
    fun getCharacteristic(serviceUuid: UUID, characteristicUuid: UUID): BluetoothGattCharacteristic {
        // Assume you have a reference to a BluetoothGatt instance
        val service = bluetoothGatt?.getService(serviceUuid)
        return service?.getCharacteristic(characteristicUuid) ?: throw IllegalArgumentException("Characteristic not found")
    }

}

