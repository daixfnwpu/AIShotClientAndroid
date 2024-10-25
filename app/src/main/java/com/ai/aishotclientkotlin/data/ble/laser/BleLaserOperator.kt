package com.ai.aishotclientkotlin.data.ble.laser

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Handler
import com.ai.aishotclientkotlin.data.ble.BLEManager
import com.ai.aishotclientkotlin.data.ble.Characteristic
import com.ai.aishotclientkotlin.util.SpManager
import com.google.ar.core.dependencies.i
import java.util.*


class BleLaserOperator private constructor(private val bleManager: BLEManager) {

    private var mOnBtConnectDeviceAddressListener: OnBtConnectDeviceAddressListener? = null
    private var mOnDataReceiveTimeoutListener: onDataReceiveTimeoutListener? = null
    private var mOnBluetoothDeviceScanListener: onBluetoothDeviceScanListener? = null
    private var mOnDataAvailableListener: OnDataAvailableListener? = null
    private var mOnBtConnectStatusListener: OnBtConnectStatusListener? = null
    private var mHandler: Handler? = null
    var isBtScanning: Boolean = false
        private set
    var isDeviceConnected: Boolean = false
        private set
    private var mTimer: Timer? = null
    private var mTimerCount = 0

    init {
        bleManager.onCharacteristicRead.add{characteristic: Characteristic,value: ByteArray ->
            mOnDataAvailableListener?.onCharacteristicRead(null, null, 0)  // Update based on your requirement
        }

        bleManager.onCharacteristicWrite.add{ charactoristic : Characteristic,bool:Boolean ->
            mOnDataAvailableListener?.onCharacteristicWrite(null, null)
             mTimerCount = 0
        }

        bleManager.onConnectionStateChanged.add { blueDevice: BluetoothDevice, connectState: Int ->
            {
                isDeviceConnected = connectState == BluetoothProfile.STATE_CONNECTED
                mOnBtConnectStatusListener?.connectStatus(isDeviceConnected)
            }
        }

       /* bleManager.setOnScanListener(object : BLEManager.OnScanListener {
            override fun onDeviceFound(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
                mOnBluetoothDeviceScanListener?.onLeScan(device, rssi, scanRecord)
            }

            override fun onScanCompleted() {
                mOnBluetoothDeviceScanListener?.onLeScanCompleted()
            }
        })*/
    }

    @SuppressLint("MissingPermission")
    fun initialize(context: Context) {
        val result = bleManager.initialize(context)
        mHandler = Handler()
        if (mTimer == null) {
            mTimer = Timer().apply {
                schedule(object : TimerTask() {
                    override fun run() {
                        mTimerCount++
                        if (mTimerCount == 4 && isDeviceConnected && mOnDataReceiveTimeoutListener != null) {
                            mOnDataReceiveTimeoutListener!!.onDataReceiveTimeout()
                        } else if (!isDeviceConnected) {
                            mTimerCount = 0
                        }
                    }
                }, 3000L, 2000L)
            }
        }
    }

    fun onDestroy() {
        mTimer?.cancel()
        mTimer = null
    }

    fun setOnBtConnectStatusListener(listener: OnBtConnectStatusListener?) {
        mOnBtConnectStatusListener = listener
    }

    fun setOnBluetoothDeviceScanListener(listener: onBluetoothDeviceScanListener?) {
        mOnBluetoothDeviceScanListener = listener
    }

    fun setOnDataAvailableListener(listener: OnDataAvailableListener?) {
        mOnDataAvailableListener = listener
    }

    fun setOnDataReceiveTimeoutListener(listener: onDataReceiveTimeoutListener?) {
        mOnDataReceiveTimeoutListener = listener
    }

    fun setOnBtConnectDeviceAddressListener(listener: OnBtConnectDeviceAddressListener?) {
        mOnBtConnectDeviceAddressListener = listener
    }


 /*   fun connect(deviceAddress: String): Boolean {
        if (isDeviceConnected) return false
        val connected = bleManager.connect(deviceAddress)
        isDeviceConnected = connected
        mOnBtConnectDeviceAddressListener?.connectDeviceAddress(deviceAddress)
        return connected
    }

    fun disconnect() {
        bleManager.disconnect()
        isDeviceConnected = false
    }*/

    fun send(data: ByteArray) {
        return bleManager.writeCharacteristic(Characteristic.lasercommunicate, data)
    }

    fun disconnect(i: Int) {
        mHandler!!.postDelayed({
            bleManager.disconnect(SpManager.Sp.BLE_LASER) }, i.toLong())
    }

    fun connect() {
       // TODO("Not yet implemented")
        bleManager.connectToDevice(SpManager.Sp.BLE_LASER)
    }

    companion object {
        const val UUID_Laser_COMMUNICATION: String = "0000ffe1-0000-1000-8000-00805f9b34fb"
        const val UUID_Laser_SERIVE: String = "0000ffe0-0000-1000-8000-00805f9b34fb"
        private var mBluetoothOperator: BleLaserOperator? = null

        fun getInstance(bleManager: BLEManager): BleLaserOperator {
            if (mBluetoothOperator == null) {
                mBluetoothOperator = BleLaserOperator(bleManager)
            }
            return mBluetoothOperator!!
        }
    }

    interface OnBtConnectDeviceAddressListener {
        fun connectDeviceAddress(address: String?)
    }

    interface OnBtConnectStatusListener {
        fun connectStatus(isConnected: Boolean)
    }

    interface OnDataAvailableListener {
        fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int)
        fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?)
    }

    interface onBluetoothDeviceScanListener {
        fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?)
        fun onLeScanCompleted()
    }

    interface onDataReceiveTimeoutListener {
        fun onDataReceiveTimeout()
    }
}
