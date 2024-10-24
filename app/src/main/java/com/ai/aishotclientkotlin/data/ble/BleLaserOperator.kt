package com.ai.aishotclientkotlin.data.ble


import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Handler
import android.util.Log
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

/* loaded from: classes.dex */
class BleLaserOperator private constructor() {
    private var mBLE: BluetoothLeClass? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
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
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mBluetoothGattService: BluetoothGattService? = null
    private var mTimer: Timer? = null
    private var mTimerCount = 0
    private val mBtConnectStatusCallback: OnBtConnectStatusListener =
        object : OnBtConnectStatusListener() {
            // from class: com.hawkeye.laser.bluetooth.BluetoothOperator.4
            // com.hawkeye.laser.bluetooth.BluetoothLeClass.OnBtConnectStatusListener
            fun onConnectStatus(z: Boolean) {
                if (this@BleLaserOperator.mOnBtConnectStatusListener != null) {
                    mOnBtConnectStatusListener!!.connectStatus(z)
                }
            }
        }
    private val mLeScanCallback =
        LeScanCallback { bluetoothDevice, i, bArr ->

            // from class: com.hawkeye.laser.bluetooth.BluetoothOperator.5
            // android.bluetooth.BluetoothAdapter.LeScanCallback
            if (this@BleLaserOperator.mOnBluetoothDeviceScanListener != null) {
                mOnBluetoothDeviceScanListener!!.onLeScan(bluetoothDevice, i, bArr)
            }
        }
    private val mOnServiceDiscover: BluetoothLeClass.OnServiceDiscoverListener =
        object : OnServiceDiscoverListener() {
            // from class: com.hawkeye.laser.bluetooth.BluetoothOperator.6
            // com.hawkeye.laser.bluetooth.BluetoothLeClass.OnServiceDiscoverListener
            fun onServiceDiscover(bluetoothGatt: BluetoothGatt?) {
                val bluetoothOperator = this@BleLaserOperator
                bluetoothOperator.displayGattServices(bluetoothOperator.mBLE.getSupportedGattServices())
            }
        }
    private val mOnDataAvaliable: OnDataAvailableListener = object : OnDataAvailableListener() {
        // from class: com.hawkeye.laser.bluetooth.BluetoothOperator.9
        // com.hawkeye.laser.bluetooth.BluetoothLeClass.OnDataAvailableListener
        override fun onCharacteristicRead(
            bluetoothGatt: BluetoothGatt?,
            bluetoothGattCharacteristic: BluetoothGattCharacteristic?,
            i: Int
        ) {
            if (this@BleLaserOperator.mOnDataAvailableListener != null) {
                mOnDataAvailableListener!!.onCharacteristicRead(
                    bluetoothGatt,
                    bluetoothGattCharacteristic,
                    i
                )
            }
        }

        // com.hawkeye.laser.bluetooth.BluetoothLeClass.OnDataAvailableListener
        override fun onCharacteristicWrite(
            bluetoothGatt: BluetoothGatt?,
            bluetoothGattCharacteristic: BluetoothGattCharacteristic?
        ) {
            if (this@BleLaserOperator.mOnDataAvailableListener != null) {
                mOnDataAvailableListener!!.onCharacteristicWrite(
                    bluetoothGatt,
                    bluetoothGattCharacteristic
                )
            }
            this@BleLaserOperator.mTimerCount = 0
        }
    }

    /* loaded from: classes.dex */
    interface OnBtConnectDeviceAddressListener {
        fun connectDeviceAddress(str: String?)
    }

    /* loaded from: classes.dex */
    interface OnBtConnectStatusListener {
        fun connectStatus(z: Boolean)
    }

    /* loaded from: classes.dex */
    interface OnDataAvailableListener {
        fun onCharacteristicRead(
            bluetoothGatt: BluetoothGatt?,
            bluetoothGattCharacteristic: BluetoothGattCharacteristic?,
            i: Int
        )

        fun onCharacteristicWrite(
            bluetoothGatt: BluetoothGatt?,
            bluetoothGattCharacteristic: BluetoothGattCharacteristic?
        )
    }

    /* loaded from: classes.dex */
    interface onBluetoothDeviceScanListener {
        fun onLeScan(bluetoothDevice: BluetoothDevice?, i: Int, bArr: ByteArray?)

        fun onLeScanCompleted()
    }

    /* loaded from: classes.dex */
    interface onDataReceiveTimeoutListener {
        fun onDataReceiveTimeout()
    }

    @SuppressLint("MissingPermission")
    fun initilize(): Boolean {
        if (!mContext!!.packageManager.hasSystemFeature("android.hardware.bluetooth_le")) {
            return false
        }
        val adapter = (mContext!!.getSystemService("bluetooth") as BluetoothManager).adapter
        this.mBluetoothAdapter = adapter
        if (adapter == null) {
            return false
        }
        if (!adapter.isEnabled) {
            mBluetoothAdapter!!.enable()
        }
        val bluetoothLeClass: BluetoothLeClass = BluetoothLeClass(mContext)
        this.mBLE = bluetoothLeClass
        if (!bluetoothLeClass.initialize()) {
            return false
        }
        mBLE.setOnServiceDiscoverListener(this.mOnServiceDiscover)
        mBLE.setOnDataAvailableListener(this.mOnDataAvaliable)
        mBLE.setOnBtConnectStatusListener(this.mBtConnectStatusCallback)
        this.mHandler = Handler()
        if (this.mTimer != null) {
            return true
        }
        val timer = Timer()
        this.mTimer = timer
        timer.schedule(object : TimerTask() {
            // from class: com.hawkeye.laser.bluetooth.BluetoothOperator.1
            // java.util.TimerTask, java.lang.Runnable
            override fun run() {
                `access$008`(this@BleLaserOperator)
                if (this@BleLaserOperator.mTimerCount == 4 && this@BleLaserOperator.isDeviceConnected && (this@BleLaserOperator.mOnDataReceiveTimeoutListener != null)) {
                    mOnDataReceiveTimeoutListener!!.onDataReceiveTimeout()
                } else {
                    if (this@BleLaserOperator.isDeviceConnected) {
                        return
                    }
                    this@BleLaserOperator.mTimerCount = 0
                }
            }
        }, 3000L, 2000L)
        return true
    }

    fun onDestroy() {
        if (this.isDeviceConnected) {
            disconnect()
        }
        try {
            mTimer!!.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        this.mTimer = null
    }

    fun setOnBtConnectStatusListener(onBtConnectStatusListener: OnBtConnectStatusListener?) {
        this.mOnBtConnectStatusListener = onBtConnectStatusListener
    }

    fun setOnBluetoothDeviceScanListener(onbluetoothdevicescanlistener: onBluetoothDeviceScanListener?) {
        this.mOnBluetoothDeviceScanListener = onbluetoothdevicescanlistener
    }

    fun setOnDataAvailableListener(onDataAvailableListener: OnDataAvailableListener?) {
        this.mOnDataAvailableListener = onDataAvailableListener
    }

    fun setOnDataReceiveTimeoutListener(ondatareceivetimeoutlistener: onDataReceiveTimeoutListener?) {
        this.mOnDataReceiveTimeoutListener = ondatareceivetimeoutlistener
    }

    fun setOnBtConnectDeviceAddressListener(onBtConnectDeviceAddressListener: OnBtConnectDeviceAddressListener?) {
        this.mOnBtConnectDeviceAddressListener = onBtConnectDeviceAddressListener
    }

    @SuppressLint("MissingPermission")
    fun scanLeDevice(z: Boolean) {
        if (z) {
            mHandler!!.postDelayed({
                this@BleLaserOperator.isBtScanning = false
                mBluetoothAdapter!!.stopLeScan(this@BleLaserOperator.mLeScanCallback)
                if (this@BleLaserOperator.mOnBluetoothDeviceScanListener != null) {
                    mOnBluetoothDeviceScanListener!!.onLeScanCompleted()
                }
            }, 10000L)
            this.isBtScanning = true
            mBluetoothAdapter!!.startLeScan(this.mLeScanCallback)
        } else {
            this.isBtScanning = false
            mBluetoothAdapter!!.stopLeScan(this.mLeScanCallback)
        }
    }

    fun IsBtEnable(): Boolean {
        return mBluetoothAdapter!!.isEnabled
    }

    fun connect(str: String?): Boolean {
        if (this.isDeviceConnected) {
            return false
        }
        val connect: Boolean = mBLE.connect(str)
        if (connect) {
            try {
                Thread.sleep(50L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            this.mBluetoothGatt = mBLE.getGatt()
            try {
                Thread.sleep(50L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }
            this.mBluetoothGattService = mBluetoothGatt!!.getService(
                UUID.fromString(
                    UUID_SERIVE
                )
            )
        }
        this.isDeviceConnected = connect
        val onBtConnectDeviceAddressListener = this.mOnBtConnectDeviceAddressListener
        onBtConnectDeviceAddressListener?.connectDeviceAddress(str)
        return connect
    }

    fun disconnect() {
        mBLE.disconnect()
        this.mBluetoothGatt = null
        this.mBluetoothGattService = null
        this.isDeviceConnected = false
    }

    fun disconnect(i: Int) {
        mHandler!!.postDelayed({ this@BleLaserOperator.disconnect() }, i.toLong())
    }

    fun send(bArr: ByteArray?): Boolean {
        var i = 0
        while (i < 5 && this.mBluetoothGattService == null) {
            try {
                this.mBluetoothGattService = mBluetoothGatt!!.getService(
                    UUID.fromString(
                        UUID_SERIVE
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            i++
        }
        val bluetoothGattService = this.mBluetoothGattService ?: return false
        try {
            val characteristic = bluetoothGattService.getCharacteristic(
                UUID.fromString(
                    UUID_COMMUNICATION
                )
            )
                ?: return false
            try {
                mBLE.setCharacteristicNotification(characteristic, true)
                characteristic.setValue(bArr)
                mBLE.writeCharacteristic(characteristic)
                return true
            } catch (e2: Exception) {
                e2.printStackTrace()
                return false
            }
        } catch (unused: Exception) {
        }
        return false
    }

    /* JADX INFO: Access modifiers changed from: private */
    fun displayGattServices(list: List<BluetoothGattService>?) {
        if (list == null) {
            return
        }
        for (bluetoothGattService in list) {
            if (bluetoothGattService.uuid.toString() != UUID_SERIVE) {
                Log.d("Gatt", "skip service uuid " + bluetoothGattService.uuid.toString())
            } else {
                bluetoothGattService.type
                val characteristic = bluetoothGattService.getCharacteristic(
                    UUID.fromString(
                        UUID_COMMUNICATION
                    )
                )
                mBLE.setCharacteristicNotification(characteristic, true)
                for (bluetoothGattDescriptor in characteristic.descriptors) {
                    bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    mBLE.writeDescriptor(bluetoothGattDescriptor)
                }
                this.mBluetoothGattService = bluetoothGattService
                mHandler!!.postDelayed({ mBLE.readCharacteristic(characteristic) }, 600L)
                mHandler!!.postDelayed({
                    characteristic.setValue(byteArrayOf(-82, -89, 4, 0, 6, 10, -68, -73))
                    mBLE.writeCharacteristic(characteristic)
                }, 500L)
            }
        }
    }

    companion object {
        private const val SCAN_PERIOD = 10000
        const val UUID_COMMUNICATION: String = "0000ffe1-0000-1000-8000-00805f9b34fb"
        const val UUID_SERIVE: String = "0000ffe0-0000-1000-8000-00805f9b34fb"
        private var mBluetoothOperator: BleLaserOperator? = null
        private var mContext: Context? = null
        fun `access$008`(bluetoothOperator: BleLaserOperator): Int {
            val i = bluetoothOperator.mTimerCount
            bluetoothOperator.mTimerCount = i + 1
            return i
        }

        fun getInstance(context: Context?): BleLaserOperator? {
            if (mBluetoothOperator == null) {
                mContext = context
                mBluetoothOperator = BleLaserOperator()
            }
            return mBluetoothOperator
        }
    }
}