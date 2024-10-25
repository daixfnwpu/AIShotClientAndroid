package com.ai.aishotclientkotlin.data.ble.laser


import android.app.Service
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import com.ai.aishotclientkotlin.util.Constants
import java.util.Timer
import java.util.TimerTask

class BleLaserService : Service(),
OnRequestPermissionsResultCallback {
    private var dataSaveServiceIntent: Intent? = null
    private var mAngleOfElevationTv: TextView? = null
    private var mBluetoothCommunication: BluetoothCommunication? = null
    private var mBluetoothOperator: BleLaserOperator? = null
    private var mDataReceiver: DataBroadcastReceiver? = null
    private var mHorizontalDistanceTv: TextView? = null
    private var mRelativeHeightTv: TextView? = null
    private var mChineseCode = "米"
    private var mUnitCode = " m"
    private var isBtConnecting = false
    private var isBtConnected = false
    private var mHandler: Handler? = null
    private var mCanSpeck = true
    private var mAutoConnectAddress: String? = null
    private val mScaleAnim: Animation = ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, 1, 0.5f, 1, 0.5f)
    private var mDelayToDisconnectTimer: Timer? = null
    private var mDelayTimerTask: TimerTask? = null
    private var connectedSecond = 0
    private var mTimeToDelayForDisconnectBluetooth: Int = 3

    // 初始化服务中的资源
    override fun onCreate() {
        super.onCreate()
        // 服务的初始化操作
        initLaserBluetooth()
    }
    var mDataAvailableListener: BleLaserOperator.OnDataAvailableListener =
        object : BleLaserOperator.OnDataAvailableListener {
            override fun onCharacteristicRead(
                bluetoothGatt: BluetoothGatt?,
                bluetoothGattCharacteristic: BluetoothGattCharacteristic?,
                i: Int
            ) {
            }

            override  fun onCharacteristicWrite(
                bluetoothGatt: BluetoothGatt?,
                bluetoothGattCharacteristic: BluetoothGattCharacteristic?
            ) {
                val value = bluetoothGattCharacteristic?.value
                if (value != null) {
                    mBluetoothCommunication?.addDataToCache(value)
                }
                value?.let { printHexString(it) }?.let { Log.d("BTWrite", it) }
            }
        }

    fun initLaserBluetooth() {
        initLaserBluetoothCallBack()
        initDelayToDisconnect()
    }

    private fun initLaserBluetoothCallBack() {
            val bluetoothCommunication: BluetoothCommunication = BluetoothCommunication(applicationContext)
            this.mBluetoothCommunication = bluetoothCommunication
            bluetoothCommunication.setReceiveShakeHandListener(object :
                BluetoothCommunication.OnReceiveShakeHandListener {
                override fun onReceiveShakeHand() {
                    mBluetoothOperator!!.send(byteArrayOf(-82, -89, 4, 0, -120, -116, -68, -73))
                }
            })
            mBluetoothOperator!!.setOnBtConnectStatusListener(object :
                BleLaserOperator.OnBtConnectStatusListener {
                override fun connectStatus(z: Boolean) {
                    mBluetoothOperator!!.send(byteArrayOf(-82, -89, 4, 0, 6, 10, -68, -73))
                }
            })
            mBluetoothOperator!!.setOnDataAvailableListener(this.mDataAvailableListener)
            mBluetoothOperator!!.setOnDataReceiveTimeoutListener(object :
                BleLaserOperator.onDataReceiveTimeoutListener {
                override fun onDataReceiveTimeout() {
                    if (mBluetoothOperator!!.isDeviceConnected) {
                        mBluetoothOperator!!.send(byteArrayOf(-82, -89, 4, 0, 7, 11, -68, -73))
                        mBluetoothOperator!!.disconnect(200)
                    }
                }
            })

            mBluetoothOperator!!.connect()
            return
    }

    private fun initDelayToDisconnect() {
        this.mDelayToDisconnectTimer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                if (!this@BleLaserService.isBtConnected) {
                    this@BleLaserService.connectedSecond = 0
                    return
                }
                if (this@BleLaserService.connectedSecond >= mTimeToDelayForDisconnectBluetooth * 60
                ) {
                    this@BleLaserService.connectedSecond = 0
                    mBluetoothOperator?.send(byteArrayOf(-82, -89, 4, 0, 7, 11, -68, -73))
                } else if (this@BleLaserService.connectedSecond % 30 == 0) {
                    mBluetoothOperator?.send(byteArrayOf(-82, -89, 4, 0, 6, 10, -68, -73))
                }
            }
        }
        this.mDelayTimerTask = timerTask
        mDelayToDisconnectTimer!!.schedule(timerTask, 10000L, 1000L)
    }

    fun remoteMeasure() {
        mBluetoothOperator?.send(byteArrayOf(-82, -89, 4, 0, 5, 9, -68, -73))
    }

    private fun connectOrDisconnect() {

        if (mBluetoothOperator?.isDeviceConnected == true) {
            mBluetoothOperator?.send(byteArrayOf(-82, -89, 4, 0, 7, 11, -68, -73))
            mBluetoothOperator?.disconnect(200)
        }
        mBluetoothOperator?.disconnect(200)
    }

//    public override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
//        if (i == 1 && i2 == -1) {
//            val string = intent!!.extras!!.getString("Device_address")
//            val bluetoothOperator: BleLaserOperator? = this.mBluetoothOperator
//            if (bluetoothOperator != null && !bluetoothOperator.isDeviceConnected) {
//                this.isBtConnecting = true
//                mBluetoothOperator?.connect()
//            }
//        }
//        super.onActivityResult(i, i2, intent)
//    }

    public  fun stop() {
        mBluetoothOperator?.onDestroy()
        mBluetoothCommunication?.onDestroy()
        try {
            unregisterReceiver(this.mDataReceiver)
        } catch (unused: Exception) {
        }
        stopService(this.dataSaveServiceIntent)
        try {
            mDelayToDisconnectTimer!!.cancel()
            mDelayTimerTask!!.cancel()
            this.mDelayToDisconnectTimer = null
            this.mDelayTimerTask = null
        } catch (unused2: Exception) {
        }
        super.onDestroy()
    }

    inner class DataBroadcastReceiver private constructor() : BroadcastReceiver() {
        // android.content.BroadcastReceiver
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Constants.DATA_ACTION_FOR_BROADCAST.equals(action)) {
                val f = intent.extras!!.getFloat(BluetoothCommunication.INTENT_TAG_LINEAR_DISTANCE)
                val f2 = intent.extras!!.getFloat(BluetoothCommunication.INTENT_TAG_ANGLE_ELEVATION)
                val f3 = intent.extras!!.getFloat(BluetoothCommunication.INTENT_TAG_VERTICAL_HEIGHT)
                val f4 = intent.extras!!.getFloat(BluetoothCommunication.INTENT_TAG_LEVEL_DISTANCE)
                val i = intent.extras!!.getInt(BluetoothCommunication.INTENT_TAG_DISTANCE_UNIT)
                if (i == 3) {
                    this@BleLaserService.mChineseCode = "英尺"
                    this@BleLaserService.mUnitCode = " f"
                } else if (i == 2) {
                    this@BleLaserService.mChineseCode = "码"
                    this@BleLaserService.mUnitCode = " y"
                } else {
                    this@BleLaserService.mChineseCode = "米"
                    this@BleLaserService.mUnitCode = " m"
                }

                mAngleOfElevationTv!!.text = "垂直仰角: $f2 °"
                mHorizontalDistanceTv!!.text = "水平距离: " + f4 + this@BleLaserService.mUnitCode
                mRelativeHeightTv!!.text = "相对高度: " + f3 + this@BleLaserService.mUnitCode
                this@BleLaserService.connectedSecond = 0
                return
            }
        }
    }

    companion object {

        fun printHexString(bArr: ByteArray): String {
            var str = ""
            for (b in bArr) {
                var hexString = Integer.toHexString(b.toInt() and 255)
                if (hexString.length == 1) {
                    hexString = "0$hexString"
                }
                str = "$str$hexString "
            }
            return str
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
        return null;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       // TODO("Not yet implemented")
    }
}