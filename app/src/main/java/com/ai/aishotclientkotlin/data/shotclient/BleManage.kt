package com.ai.aishotclientkotlin.data.shotclient

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import timber.log.Timber
import java.util.*
@SuppressLint("MissingPermission")
class BLEManager(private val context: Context) {

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private var bluetoothGatt: BluetoothGatt? = null
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("BLE_Preferences", Context.MODE_PRIVATE)

    // GATT回调
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Timber.tag("BLE").e("Connected to GATT server.")
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    gatt?.discoverServices() // 发现服务


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Timber.tag("BLE").e("Disconnected from GATT server.")
                bluetoothGatt?.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.tag("BLE").e("Services discovered")
                // 获取服务并处理特征
                val service = gatt?.getService(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"))
                val characteristic = service?.getCharacteristic(UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb"))
                if (characteristic != null) {
                    readCharacteristic(characteristic)
                }
            } else {
                Timber.tag("BLE").e("onServicesDiscovered received: " + status)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic?.value
                Timber.tag("BLE").e("Characteristic read: " + data?.joinToString(","))
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

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            val data = characteristic?.value
            Timber.tag("BLE").e("Received data: " + data?.joinToString(","))
        }
    }


    fun startScan(onDeviceFound: (BluetoothDevice) -> Unit) {
        bluetoothLeScanner?.startScan(object : ScanCallback() {
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

    fun connectToDevice(deviceAddress: String) {
        val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // 设备连接成功
                    Log.e("ble","设备连接成功")
                    saveDeviceAddress(deviceAddress)
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // 设备断开连接
                    bluetoothGatt?.close()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                // 发现服务
            }
        })
    }
    private fun saveDeviceAddress(deviceAddress: String) {
        sharedPreferences.edit {
            putString("LAST_CONNECTED_DEVICE", deviceAddress)
        }
    }

    fun reconnectLastDevice() {
        val lastDeviceAddress = sharedPreferences.getString("LAST_CONNECTED_DEVICE", null)
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
}

